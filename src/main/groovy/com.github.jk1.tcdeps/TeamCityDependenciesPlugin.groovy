package com.github.jk1.tcdeps

import org.gradle.api.Plugin
import org.gradle.api.Project

class TeamCityDependenciesPlugin implements Plugin<Project> {

    private def builder = new RepositoryBuilder()

    @Override
    void apply(Project project) {
        project.extensions.add("teamcity", new ConfigurationExtension())
        project.ext.tc = { String buildTypeId, String version, String artifactPath ->
            def descriptor = new ArtifactDescriptor(artifactPath)
            if (descriptor.hasPath()){
                builder.addArtifactPattern(descriptor.getPath())
            }
            return ["org:$buildTypeId:$version", { ->
                artifact {
                    name = descriptor.getName()
                    type = descriptor.getExtension()
                }
            }]
        }
        project.afterEvaluate {
            builder.setTeamCityUrl(project.teamcity.url)
            builder.createRepository(project)
        }
    }
}
