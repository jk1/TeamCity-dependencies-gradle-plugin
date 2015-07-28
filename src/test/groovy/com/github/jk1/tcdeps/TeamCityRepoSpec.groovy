package com.github.jk1.tcdeps

import com.github.jk1.tcdeps.repository.TeamCityIvyRepository
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * Created by Nikita.Skvortsov
 * date: 24.07.2015.
 */
class TeamCityRepoSpec extends Specification {

    def "plugin should provide 'teamcity' repository notation"() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.github.jk1.tcdeps'

        when:
        project.repositories.teamcityServer()

        then:
        project.repositories.findByName("TeamCity") instanceof TeamCityIvyRepository
        !project.repositories.findByName("TeamCity").pin.pinEnabled
    }


    def "teamcity repository should properly append path to configured url"() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.github.jk1.tcdeps'

        when:
        project.repositories.teamcityServer {
            url urlValue
        }

        then:
        project.repositories.findByName("TeamCity").url == patchedValue

        where:
        urlValue                                        | patchedValue
        "http://teamcity"                               | new URI("http://teamcity/httpAuth/repository/download")
        "http://teamcity/guestAuth/repository/download" | new URI("http://teamcity/guestAuth/repository/download")
    }



    def "teamcity repository should support pin configuration"() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.github.jk1.tcdeps'

        when:
        project.repositories.teamcityServer {
            url "http://teamcity/httpAuth/repository/download"
            pin {
                // pinning usually requires authentication
                username = "name"
                password = "secret"
                stopBuildOnFail = true  // not mandatory, default to 'false'
                message = "Pinned for MyCoolProject"  // not mandatory
            }
        }
        def repo = project.repositories.findByName("TeamCity")

        then:
        repo.url.toString() == "http://teamcity/httpAuth/repository/download"
        repo.pin.pinEnabled
        repo.pin.username == "name"
        repo.pin.password == "secret"
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
        def repos = project.repositories.findAll { it instanceof TeamCityIvyRepository }
        repos.size() == 1
        repos.get(0).url.toString().contains("teamcity2")
    }
}
