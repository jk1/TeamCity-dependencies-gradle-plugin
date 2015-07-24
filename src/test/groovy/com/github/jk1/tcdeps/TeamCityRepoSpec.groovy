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
    project.repositories.teamcity()

    then:
    project.repositories.findByName("TeamCity") instanceof TeamCityIvyRepository
  }


  def "teamcity repository should properly append path to configured url"() {
    Project project = ProjectBuilder.builder().build()
    project.pluginManager.apply 'com.github.jk1.tcdeps'

    when:
    project.repositories.teamcity {
      url urlValue
    }

    then:
    project.repositories.findByName("TeamCity").url == patchedValue

    where:
    urlValue                                        | patchedValue
    "http://teamcity"                               | new URI("http://teamcity/httpAuth/repository/download")
    "http://teamcity/guestAuth/repository/download" | new URI("http://teamcity/guestAuth/repository/download")
  }
}
