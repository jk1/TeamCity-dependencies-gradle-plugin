package com.github.jk1.tcdeps

import org.apache.commons.collections.Closure
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency

class TeamCityDependenciesPlugin implements Plugin<Project> {

    private def builder = new RepositoryBuilder()

    @Override
    void apply(Project project) {
        project.repositories.ext
        project.extensions.add("teamcityServer", new ConfigurationExtension())
        project.ext.tc = { Object notation ->
            return addDependency(new DependencyDescriptor(notation))
        }
        project.afterEvaluate {
            builder.setTeamCityUrl(project.teamcityServer.url)
            builder.createRepository(project)
        }
    }

    private Object addDependency(DependencyDescriptor descriptor){
        if (descriptor.artifactDescriptor.hasPath()){
            builder.addArtifactPattern(descriptor.artifactDescriptor.path)
        }
        return ["org:$descriptor.buildTypeId:$descriptor.version", { ->
            artifact {
                name = descriptor.artifactDescriptor.name
                type = descriptor.artifactDescriptor.extension
            }
        }]
    }
}
