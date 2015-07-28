package com.github.jk1.tcdeps.repository

/**
 * Created by Nikita.Skvortsov
 * date: 28.07.2015.
 */
class PinConfiguration {
    TeamCityIvyRepository repo
    String username
    String password
    boolean stopBuildOnFail
    boolean pinEnabled
    String message

    PinConfiguration(TeamCityIvyRepository repository) {
        repo = repository
    }

    String getUrl() {
        repo.getUrl().toString()
    }

    def setDefaultMessage(String message) {
        if (this.message == null) {
            this.message = message
        }
    }
}
