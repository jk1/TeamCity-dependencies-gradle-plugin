package com.github.jk1.tcdeps

import org.gradle.api.Plugin
import org.gradle.api.Project

class TeamCityDependenciesPlugin implements Plugin<Project> {

    private builder = new RepositoryBuilder()
    private pinner = new DepedencyPinner()

    @Override
    void apply(Project project) {
        project.repositories.ext
        project.extensions.add("teamcityServer", new PluginConfiguration())
        project.ext.tc = { Object notation ->
            return addDependency(new DependencyDescriptor(notation))
        }
        project.afterEvaluate {
            builder.configure(project)
            pinner.configure(project)
            builder.process()
            pinner.process()
        }
    }

    private Object addDependency(DependencyDescriptor descriptor){
        builder.addDependency(descriptor)
        pinner.addDependency(descriptor)
        return ["org:$descriptor.buildTypeId:$descriptor.version", { ->
            artifact {
                name = descriptor.artifactDescriptor.name
                type = descriptor.artifactDescriptor.extension
            }
        }]
    }
}
