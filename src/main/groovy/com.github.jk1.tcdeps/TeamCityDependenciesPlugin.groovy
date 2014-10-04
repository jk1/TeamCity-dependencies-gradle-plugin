package com.github.jk1.tcdeps

import org.gradle.api.Plugin
import org.gradle.api.Project

class TeamCityDependenciesPlugin implements Plugin<Project> {

    private builder = new RepositoryBuilder()
    private pinner = new DepedencyPinner()

    @Override
    void apply(Project project) {
        project.repositories.ext
        project.extensions.add("teamcityServer", new ConfigurationExtension())
        project.ext.tc = { Object notation ->
            return addDependency(new DependencyDescriptor(notation))
        }
        project.afterEvaluate {
            builder.setTeamCityUrl(project.teamcityServer.url)
            pinner.setConfig(project.teamcityServer)
            builder.createRepository(project)
            pinner.pinAllBuilds()
        }
    }

    private Object addDependency(DependencyDescriptor descriptor){
        if (descriptor.artifactDescriptor.hasPath()){
            builder.addArtifactPattern(descriptor.artifactDescriptor.path)
        }
        pinner.addDependency(descriptor)
        return ["org:$descriptor.buildTypeId:$descriptor.version", { ->
            artifact {
                name = descriptor.artifactDescriptor.name
                type = descriptor.artifactDescriptor.extension
            }
        }]
    }
}
