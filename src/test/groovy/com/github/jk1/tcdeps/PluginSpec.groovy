package com.github.jk1.tcdeps

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class PluginSpec extends Specification {

    def "plugin should be applicable to a project"(){
        Project project = ProjectBuilder.builder().build()

        when:
        project.pluginManager.apply 'com.github.jk1.tcdeps'

        then:
        project.repositories.teamcityServer
    }
}
