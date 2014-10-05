package com.github.jk1.tcdeps

import org.gradle.api.Plugin
import org.gradle.api.Project

class TeamCityDependenciesPlugin implements Plugin<Project> {

    private processors = [new RepositoryBuilder(), new DepedencyPinner()]

    @Override
    void apply(Project project) {
        project.repositories.ext
        project.extensions.add("teamcityServer", new PluginConfiguration())
        project.ext.tc = { Object notation ->
            return addDependency(new DependencyDescriptor(notation))
        }
        project.afterEvaluate {
            processors.each {
                it.configure(project)
                it.process()
            }
        }
    }

    private Object addDependency(DependencyDescriptor descriptor) {
        processors.each { it.addDependency(descriptor) }
        return descriptor.toDependencyNotation()
    }
}
