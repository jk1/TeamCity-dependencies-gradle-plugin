package com.github.jk1.tcdeps.repository

import org.gradle.api.Action
import org.gradle.api.artifacts.repositories.IvyArtifactRepository
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.internal.artifacts.BaseRepositoryFactory
import org.gradle.util.ConfigureUtil

class TeamCityRepositoryFactory {

    private final BaseRepositoryFactory repositoryFactory

    TeamCityRepositoryFactory(BaseRepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory
    }

    def IvyArtifactRepository createTeamCityRepo() {

        def IvyArtifactRepository repo = createDefaultRepo()

        def config = new PinConfiguration()
        repo.metaClass.pinConfig = config
        repo.metaClass.baseTeamCityURL = ""
        repo.metaClass.getPin = { -> return pinConfig }
        repo.metaClass.pin = { Closure pinConfigClosure ->
            pinConfig.pinEnabled = true;
            pinConfigClosure.setDelegate(pinConfig)
            pinConfigClosure.call()
        }

        def oldSetUrl = repo.&setUrl
        def oldCredentials = repo.&credentials

        repo.metaClass.setUrl = { Object url ->
            baseTeamCityURL = url as String;
            config.url = normalizeUrl(url)
            if (getConfiguredCredentials() != null) {
                oldSetUrl(normalizeUrl(url) + "httpAuth/repository/download")
            } else {
                oldSetUrl(normalizeUrl(url) + "guestAuth/repository/download")
            }
        }

        repo.metaClass.credentials = { Closure action ->
            oldCredentials(new CredentialsConfigurationAction(actionClosure: action))
            oldSetUrl(normalizeUrl(baseTeamCityURL) + "httpAuth/repository/download")
        }

        return repo
    }

    private def createDefaultRepo() {
        def IvyArtifactRepository repo = repositoryFactory.createIvyRepository()
        repo.layout('pattern', {
            artifact '[module]/[revision]/[artifact](.[ext])'
            ivy '[module]/[revision]/teamcity-ivy.xml'
        })
        return repo
    }

    private def normalizeUrl(String url) {
        return url.endsWith("/") ? url : url + "/"
    }

    private class CredentialsConfigurationAction implements Action<PasswordCredentials> {

        def Closure actionClosure

        @Override
        void execute(PasswordCredentials passwordCredentials) {
            ConfigureUtil.configure(actionClosure, passwordCredentials)
        }
    }
}
