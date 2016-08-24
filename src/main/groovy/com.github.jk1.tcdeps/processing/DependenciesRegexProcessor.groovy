package com.github.jk1.tcdeps.processing

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencyArtifact
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.result.ComponentArtifactsResult
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.parser.DisconnectedDescriptorParseContext
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.parser.DownloadedIvyModuleDescriptorParser
import org.gradle.internal.component.external.model.DefaultModuleComponentArtifactMetadata
import org.gradle.ivy.IvyDescriptorArtifact
import org.gradle.ivy.IvyModule

class DependenciesRegexProcessor {

    def Project project
    def logger

    DependenciesRegexProcessor(Project project) {
        this.project = project
        this.logger = project.logger
    }

    void processDependencies(Configuration configuration) {
        logger.debug("processing $project, $configuration")

        def ivyDescriptors = getIvyDescriptorsForConfiguration(configuration.copy())

        ivyDescriptors.findAll { hasIvyArtifact(it) }.each { component ->

            def ivyFile = getIvyArtifact(component);
            // TODO or should it be multiple dependencies per component?
            def ModuleDependency targetDependency = findRelatedDependency(component, configuration)

            logger.debug("Dependency [$targetDependency] has ivy file [$ivyFile], parsing")

            def ivyDefinedArtifacts = readArtifactsSet(ivyFile, project)

            logger.debug("parsed, matching dependencies")

            def Set<DependencyArtifact> depArtifacts = targetDependency.getArtifacts();
            def Set<DefaultModuleComponentArtifactMetadata> toAdd = new HashSet<>()
            def i = depArtifacts.iterator()
            while (i.hasNext()) {
                def DependencyArtifact da = i.next()
                def daName = "${da.name}.${da.type}".toString()
                def candidates = []
                logger.debug("processing dependency artifact [${daName}]")

                def exactEqual = ivyDefinedArtifacts.find {
                    if (daName.equals(it.name.toString())) {
                        return true
                    } else {
                        if (it.name.toString() ==~ $/${daName}/$) {
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

            toAdd.each { DefaultModuleComponentArtifactMetadata artifactMD ->
                logger.debug("injecting new artifact [${artifactMD.name.name}.${artifactMD.name.extension}]")
                targetDependency.artifact {
                    name = artifactMD.name.name
                    type = artifactMD.name.extension
                }
            }
        }
    }

    private Set<DefaultModuleComponentArtifactMetadata> readArtifactsSet(File ivyFile, Project project) {
        project.logger.debug("Parsing ivy file [$ivyFile]")
        new DownloadedIvyModuleDescriptorParser()
            .parseMetaData(new DisconnectedDescriptorParseContext(), ivyFile)
            .getConfiguration("default")
            .artifacts
    }

    private ModuleDependency findRelatedDependency(component, configuration) {
        (ModuleDependency) configuration.dependencies.find { dep ->
            dep instanceof ModuleDependency &&
                "${dep.group}:${dep.name}:${dep.version}".toString().equals(component.id.displayName)
        }
    }

    private boolean hasIvyArtifact(ComponentArtifactsResult component) {
        def File ivyFile = getIvyArtifact(component)
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

    def process() {
        project.configurations.all {
            project.logger.debug("Post-processing dependency configuration $it")
            processDependencies(it)
        }
    }
}
