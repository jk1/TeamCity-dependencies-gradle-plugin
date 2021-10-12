package com.github.jk1.tcdeps.repository

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.IvyArtifactRepository
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.provider.Property
import org.gradle.util.ConfigureUtil

class TeamCityRepositoryFactory {

    IvyArtifactRepository createTeamCityRepo(Project project) {

        IvyArtifactRepository repo = createDefaultRepo(project)

        PinConfiguration config = new PinConfiguration()
        repo.metaClass.pinConfig = config
        repo.metaClass.baseTeamCityURL = ""
        repo.metaClass.getPin = { -> return pinConfig }
        repo.metaClass.pin = { Closure pinConfigClosure ->
            pinConfig.pinEnabled = true
            pinConfigClosure.setDelegate(pinConfig)
            pinConfigClosure.call()
        }

        def oldSetUrl = repo.&setUrl
        def oldCredentials = repo.&credentials

        repo.metaClass.setUrl = { Object url ->
            baseTeamCityURL = url as String
            config.url = normalizeUrl(url)
            if (getConfiguredCredentials() instanceof Property) {
                if (getConfiguredCredentials().isPresent()) {
                    oldSetUrl(normalizeUrl(url) + "httpAuth/repository/download")
                } else {
                    oldSetUrl(normalizeUrl(url) + "guestAuth/repository/download")
                }
            } else {
                if (getConfiguredCredentials() != null) {
                    oldSetUrl(normalizeUrl(url) + "httpAuth/repository/download")
                } else {
                    oldSetUrl(normalizeUrl(url) + "guestAuth/repository/download")
                }
            }
        }

        repo.metaClass.credentials = { Closure action ->
            oldCredentials(new CredentialsConfigurationAction(actionClosure: action))
            oldSetUrl(normalizeUrl(baseTeamCityURL) + "httpAuth/repository/download")
        }

        return repo
    }

    private IvyArtifactRepository createDefaultRepo(Project project) {
        return project.repositories.ivy {
            name = 'TeamCity'
            patternLayout {
                artifact '[module]/[revision]/[artifact](.[ext])'
                ivy '[module]/[revision]/teamcity-ivy.xml'
            }
        }
    }

    private String normalizeUrl(String url) {
        return url.endsWith("/") ? url : url + "/"
    }

    private class CredentialsConfigurationAction implements Action<PasswordCredentials> {

        Closure actionClosure

        @Override
        void execute(PasswordCredentials passwordCredentials) {
            ConfigureUtil.configure(actionClosure, passwordCredentials)
        }
    }
}
