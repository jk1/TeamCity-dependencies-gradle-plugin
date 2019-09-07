package com.github.jk1.tcdeps.processing

import com.github.jk1.tcdeps.util.ResourceLocator
import groovy.transform.Canonical
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencyArtifact
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.result.ComponentArtifactsResult
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.ivy.IvyDescriptorArtifact
import org.gradle.ivy.IvyModule

import static com.github.jk1.tcdeps.util.ResourceLocator.logger
import static com.github.jk1.tcdeps.util.ResourceLocator.project

class ArtifactRegexResolver {

    def process() {
        try {
            // make configuration resolution as lazy, as possible
            project.configurations.findAll { it.state != Configuration.State.UNRESOLVED }.each { configuration ->
                resolveArtifacts(configuration)
            }
            def capturedProject = project
            project.configurations.findAll { it.state == Configuration.State.UNRESOLVED }.each { configuration ->
                configuration.incoming.beforeResolve { incoming ->
                    // Make sure the closure won't run on configuration copy. See https://github.com/gradle/gradle/pull/1603
                    if (incoming == configuration.incoming) {
                        ResourceLocator.setContext(capturedProject)
                        resolveArtifacts(configuration)
                    }
                }
            }
        } catch (Throwable e) {
            /*
            * The code below depends on internal Gradle classes and is therefore quite fragile.
            * Artifact wildcard are not used by most of plugin users so  we don't want the whole
            * build to fail if something got changed in future Gradle versions.
            * Just log the error and skip wildcard resolution step instead.
             */
            logger.warn('An error occurred during artifact notation pattern resolution', e)
        }
    }

    def resolveArtifacts(Configuration configuration) {
        logger.debug("Processing $project, $configuration")

        def ivyDescriptors = getIvyDescriptorsForConfiguration(configuration.copy())

        ivyDescriptors.findAll { hasIvyArtifact(it) }.each { component ->

            def ivyFile = getIvyArtifact(component)
            // TODO or should it be multiple dependencies per component?
            ModuleDependency targetDependency = findRelatedDependency(component, configuration)

            if (targetDependency != null) {
                logger.debug("Dependency [$targetDependency] has ivy file [$ivyFile], parsing")

                def ivyDefinedArtifacts = readArtifactsSet(ivyFile)
                Set<IvyArtifactName> toAdd = new HashSet<>()
                def i = targetDependency.getArtifacts().iterator()
                while (i.hasNext()) {
                    DependencyArtifact da = i.next()
                    String daName = "${da.name}.${da.type}".toString()
                    def candidates = []
                    logger.debug("processing dependency artifact [${daName}]")
                    def exactEqual = ivyDefinedArtifacts.find {
                        if (daName == it.toString()) {
                            return true
                        } else {
                            if (it.toString() ==~ $/${daName}/$) {
                                candidates.add(it)
                            }
                            return false
                        }
                    }
                    logger.debug("got exact equal [${exactEqual}] and candidates [${candidates}]")
                    def hasMatches = candidates.size() > 0
                    if (exactEqual == null && hasMatches) {
                        i.remove()
                        toAdd.addAll(candidates)
                    }
                }

                toAdd.each { IvyArtifactName artifact ->
                    logger.debug("injecting new artifact [${artifact.toString()}]")
                    targetDependency.artifact {
                        name = artifact.name
                        type = artifact.extension
                    }
                }
            }
        }
    }

    private Set<IvyArtifactName> readArtifactsSet(File ivyFile) {
        project.logger.debug("Parsing ivy file [$ivyFile]")
        def ivyModule = new XmlSlurper().parseText(ivyFile.text)
        return ivyModule.publications.childNodes().collect {
            new IvyArtifactName(name: it.attributes().get("name"), extension: it.attributes().get("ext"))
        }
    }

    private ModuleDependency findRelatedDependency(component, configuration) {
        (ModuleDependency) configuration.dependencies.find { dep ->
            dep instanceof ModuleDependency &&
                    "$dep.group:$dep.name:$dep.version" == component.id.displayName
        }
    }

    private boolean hasIvyArtifact(ComponentArtifactsResult component) {
        File ivyFile = getIvyArtifact(component)
        if (ivyFile == null) {
            logger.debug("No ivy descriptor for component [$component.id.displayName].")
            false
        }
        true
    }

    private File getIvyArtifact(ComponentArtifactsResult component) {
        return component.getArtifacts(IvyDescriptorArtifact).find { it instanceof ResolvedArtifactResult }?.file
    }

    private Set<ComponentArtifactsResult> getIvyDescriptorsForConfiguration(Configuration configuration) {
        def componentIds = configuration.incoming.resolutionResult.allDependencies
                .findAll { it instanceof ResolvedDependencyResult }
                .collect { it.selected.id }

        if (componentIds.isEmpty()) {
            logger.debug("no components found")
            return []
        } else {
            logger.debug("component ids $componentIds")
        }
        getIvyDescriptorsForComponents(componentIds)
    }

    private Set<ComponentArtifactsResult> getIvyDescriptorsForComponents(List componentIds) {
        project.dependencies.createArtifactResolutionQuery()
                .forComponents(componentIds)
                .withArtifacts(IvyModule, IvyDescriptorArtifact)
                .execute().resolvedComponents
    }

    @Canonical
    private class IvyArtifactName {

        String name
        String extension

        @Override
        String toString() {
            return extension == null ? name : "$name.$extension"
        }
    }
}
