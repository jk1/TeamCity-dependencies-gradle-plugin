package com.github.jk1.tcdeps

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency


class RepositoryBuilder {

    private final TC_DOWNLOAD_PATH = 'guestAuth/repository/download'
    private final patterns = new ArrayList<String>()
    private String teamCityUrl

    public void setTeamCityUrl(String teamCityUrl) {
        this.teamCityUrl = teamCityUrl
    }

    public void addArtifactPattern(String relativePath) {
        println('Adding pattern ' + "[module]/[revision]/$relativePath[artifact](.[ext])")
        patterns.add("[module]/[revision]/$relativePath[artifact](.[ext])")
    }

    public void createRepository(Project project) {
        project.repositories.ivy {
            url "$teamCityUrl/$TC_DOWNLOAD_PATH"
            layout "pattern", {
                ivy '[module]/[revision]/teamcity-ivy.xml'
                artifact '[module]/[revision]/[artifact](.[ext])'
                patterns.each {
                    pattern -> artifact pattern
                }
            }
        }
    }
}
