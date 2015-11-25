package com.github.jk1.tcdeps.repository

import org.gradle.api.artifacts.repositories.IvyArtifactRepository

/**
 * Created by Nikita.Skvortsov
 * date: 28.07.2015.
 */
class PinConfiguration {
    IvyArtifactRepository repo
    String username
    String password
    boolean stopBuildOnFail
    boolean pinEnabled
    String message

    PinConfiguration(IvyArtifactRepository repository) {
        repo = repository
    }

    String getUrl() {
        repo.baseTeamCityURL
    }

    def setDefaultMessage(String message) {
        if (this.message == null) {
            this.message = message
        }
    }
}
