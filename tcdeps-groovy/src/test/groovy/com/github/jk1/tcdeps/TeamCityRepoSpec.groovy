package com.github.jk1.tcdeps


import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.IvyArtifactRepository
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class TeamCityRepoSpec extends Specification {

    def "plugin should provide 'teamcity' repository notation"() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.github.jk1.tcdeps'

        when:
        project.repositories.teamcityServer()

        then:
        project.repositories.findByName("TeamCity") instanceof IvyArtifactRepository
        !project.repositories.findByName("TeamCity").pin.pinEnabled
    }


    def "teamcity repository should use guest auth urls when no username is available"() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.github.jk1.tcdeps'

        when:
        project.repositories.teamcityServer {
            url = "http://teamcity"
        }

        then:
        project.repositories.findByName("TeamCity").getUrl() == new URI("http://teamcity/guestAuth/repository/download")
    }

    def "teamcity repository should use http auth when credentials are provided"() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.github.jk1.tcdeps'

        when:
        project.repositories.teamcityServer {
            url = "http://teamcity"
            credentials {
                username "name"
                password "secret"
            }
        }

        then:
        project.repositories.findByName("TeamCity").getUrl() == new URI("http://teamcity/httpAuth/repository/download")
    }



    def "teamcity repository should support pin configuration"() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.github.jk1.tcdeps'

        when:
        project.repositories.teamcityServer {
            url = "http://teamcity"
            credentials {
                username = "name"
                password = "secret"
            }
            pin {
                stopBuildOnFail = true
                message = "Pinned for MyCoolProject"
            }
        }
        def repo = project.repositories.findByName("TeamCity")

        then:
        repo.getUrl().toString() == "http://teamcity/httpAuth/repository/download"
        repo.credentials.username == "name"
        repo.credentials.password == "secret"
        repo.pin.pinEnabled
        repo.pin.message == "Pinned for MyCoolProject"
        repo.pin.stopBuildOnFail
    }

    def "Should maintain only one TeamCity server"() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.github.jk1.tcdeps'

        when:
        project.repositories.teamcityServer {
            url "http://teamcity1"
        }
        project.repositories.teamcityServer {
            url "http://teamcity2"
        }

        then:
        def repos = project.repositories.findAll { it instanceof IvyArtifactRepository }
        repos.size() == 1
        repos.get(0).getUrl().toString().contains("teamcity2")
    }
}
