package com.github.jk1.tcdeps

import org.gradle.api.Plugin
import org.gradle.api.Project

class TeamCityDependenciesPlugin implements Plugin<Project> {

    private processors

    @Override
    void apply(Project project) {
        processors = [new RepositoryBuilder(), new DepedencyPinner()]
        project.extensions.add("teamcityServer", new PluginConfiguration())
        project.ext.tc = { Object notation ->
            return addDependency(new DependencyDescriptor(notation))
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
        return descriptor.toDependencyNotation()
    }
}
