package com.github.jk1.tcdeps

import com.github.jk1.tcdeps.model.DependencyDescriptor
import com.github.jk1.tcdeps.processing.ChangingModuleVersionResolver
import com.github.jk1.tcdeps.processing.DepedencyPinner
import com.github.jk1.tcdeps.processing.RepositoryBuilder
import org.gradle.api.Plugin
import org.gradle.api.Project

class TeamCityDependenciesPlugin implements Plugin<Project> {

    private processors

    @Override
    void apply(Project project) {
        LogFacade.logger = project.logger
        processors = [new ChangingModuleVersionResolver(), new RepositoryBuilder(), new DepedencyPinner()]
        project.extensions.add("teamcityServer", new PluginConfiguration())
        project.ext.tc = { Object notation ->
            return addDependency(DependencyDescriptor.create(notation))
        }
        processors.each {
            it.configure(project)
        }
        project.afterEvaluate {
            processors.each {
                it.process()
            }
        }
    }

    private Object addDependency(DependencyDescriptor descriptor) {
        processors.each { it.addDependency(descriptor) }
        def notation = descriptor.toDependencyNotation()
        LogFacade.debug("Dependency generated: $notation")
        return notation
    }
}
