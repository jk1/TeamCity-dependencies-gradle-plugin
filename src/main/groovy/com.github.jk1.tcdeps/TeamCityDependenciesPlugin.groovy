package com.github.jk1.tcdeps

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency

/**
 * Created by jk1 on 20.09.14.
 */
class TeamCityDependenciesPlugin implements Plugin<Project> {

    private def TC_DOWNLOAD_PATH = 'guestAuth/repository/download'

    @Override
    void apply(Project project) {
        project.extensions.add("teamcity", new ConfigurationExtension())
        project.afterEvaluate {
            project.configurations.collect {
                conf ->
                    println("Observing configuration " + conf)
                    conf.allDependencies.findAll {
                        dep -> dep.hasProperty('path')
                    }
            }.flatten().each {
                dep ->
                    addRepository(project, dep)
            }
        }
    }

    private void addRepository(Project project, Dependency dep) {
        println("Adding repo for dep " + dep)
        def url = project.teamcity.url
        project.repositories.ivy {
            ivyPattern "$url/$TC_DOWNLOAD_PATH/[module]/[revision]/teamcity-ivy.xml"
            artifactPattern "$url/$TC_DOWNLOAD_PATH/[module]/[revision]/$dep.path"
        }
    }
}
