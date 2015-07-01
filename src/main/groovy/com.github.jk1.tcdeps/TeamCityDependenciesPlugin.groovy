package com.github.jk1.tcdeps

import com.github.jk1.tcdeps.model.DependencyDescriptor
import com.github.jk1.tcdeps.processing.ModuleVersionResolver
import com.github.jk1.tcdeps.processing.DepedencyPinner
import com.github.jk1.tcdeps.processing.RepositoryBuilder
import org.gradle.api.Plugin
import org.gradle.api.Project

import static com.github.jk1.tcdeps.util.ResourceLocator.*

class TeamCityDependenciesPlugin implements Plugin<Project> {

    private processors

    @Override
    void apply(Project theProject) {
        processors = [new ModuleVersionResolver(), new RepositoryBuilder(), new DepedencyPinner()]
        theProject.extensions.add("teamcityServer", new PluginConfiguration())
        initResourceLocator(theProject)
        theProject.ext.tc = { Object notation ->
            theProject.teamcityServer.assertConfigured()
            return addDependency(DependencyDescriptor.create(notation))
        }
        theProject.afterEvaluate { processors.each { it.process() } }
        theProject.gradle.buildFinished { closeResourceLocator() }
    }

    private Object addDependency(DependencyDescriptor descriptor) {
        processors.each { it.addDependency(descriptor) }
        def notation = descriptor.toDependencyNotation()
        logger.debug("Dependency generated: $notation")
        return notation
    }
}
