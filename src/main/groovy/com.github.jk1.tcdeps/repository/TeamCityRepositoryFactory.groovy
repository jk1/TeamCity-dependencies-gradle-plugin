package com.github.jk1.tcdeps.repository

import org.gradle.api.Action
import org.gradle.api.artifacts.repositories.IvyArtifactRepository
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.internal.artifacts.BaseRepositoryFactory
import org.gradle.util.ConfigureUtil

/**
 * Created by Nikita.Skvortsov
 * date: 24.07.2015.
 */
class TeamCityRepositoryFactory {
    private final BaseRepositoryFactory repositoryFactory

    TeamCityRepositoryFactory(BaseRepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory
    }

    def IvyArtifactRepository createTeamCityRepo() {
        def IvyArtifactRepository repo = repositoryFactory.createIvyRepository()
        repo.layout('pattern' , {
            artifact '[module]/[revision]/[artifact](.[ext])'
            ivy '[module]/[revision]/teamcity-ivy.xml'
        })

        repo.metaClass.pinConfig = new PinConfiguration(repo);
        repo.metaClass.baseTeamCityURL = ""

        repo.metaClass.getPin = {->
            return pinConfig
        }

        repo.metaClass.pin = { Closure pinConfigClosure ->
            pinConfig.pinEnabled = true;
            pinConfigClosure.setDelegate(pinConfig)
            pinConfigClosure.call()
        }

        def oldSetUrl = repo.&setUrl
        def oldCredentials = repo.&credentials

        repo.metaClass.setUrl = { Object url ->
            baseTeamCityURL = url as String;
            if (getConfiguredCredentials() != null) {
                oldSetUrl(url + (url.endsWith("/") ? "" : "/") + "httpAuth/repository/download")
            } else {
                oldSetUrl(url + (url.endsWith("/") ? "" : "/") + "guestAuth/repository/download")
            }
        }


        repo.metaClass.credentials = { Closure action ->
            oldCredentials(new Action<PasswordCredentials>(){
                @Override
                void execute(PasswordCredentials passwordCredentials) {
                    ConfigureUtil.configure(action, passwordCredentials)
                }
            })
            oldSetUrl(baseTeamCityURL + (baseTeamCityURL.endsWith("/") ? "" : "/") + "httpAuth/repository/download")
        }

        return repo
    }
}
