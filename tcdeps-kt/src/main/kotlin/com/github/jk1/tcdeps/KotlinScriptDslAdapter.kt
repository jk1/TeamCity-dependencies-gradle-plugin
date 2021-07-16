package com.github.jk1.tcdeps

import com.github.jk1.tcdeps.model.DependencyDescriptor
import com.github.jk1.tcdeps.repository.PinConfiguration
import com.github.jk1.tcdeps.util.ResourceLocator
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.IvyArtifactRepository
import org.gradle.internal.artifacts.repositories.AuthenticationSupportedInternal

object KotlinScriptDslAdapter {

    fun RepositoryHandler.teamcityServer(action: IvyArtifactRepository.() -> Unit): IvyArtifactRepository {
        val project = ResourceLocator.getProject()
        val plugin = project.getOurPlugin()
        val repositories = project.repositories
        val oldRepo = repositories.findByName("TeamCity") as IvyArtifactRepository?
        val repo = plugin.createTeamCityRepository(project)
        action(repo)
        if (repo.url == null) {
            throw GradleException("TeamCity repository url shouldn't be null")
        }
        ResourceLocator.getConfig().url = repo.url.toString()
        if (oldRepo != null) {
            project.logger.warn("Project $project already has TeamCity server ${oldRepo.url}, overriding with ${repo.url}")
            repositories.remove(oldRepo)
        }

        val tcUrl = repo.url.toString()
        val normalizeTcUrl = if (tcUrl.endsWith('/')) tcUrl else "$tcUrl/"

        if (repo.credentials.username.orEmpty().isBlank() && repo.credentials.password.orEmpty().isBlank()) {
            (repo as AuthenticationSupportedInternal).setConfiguredCredentials(null)
            repo.setUrl("${normalizeTcUrl}guestAuth/repository/download")
        } else {
            repo.setUrl("${normalizeTcUrl}httpAuth/repository/download")
        }
        repositories.add(repo)
        return repo

    }

    fun IvyArtifactRepository.pin(action: PinConfiguration.() -> Unit) {
        val pinConfig = ResourceLocator.getConfig()
        action(pinConfig)
        pinConfig.pinEnabled = true
        ResourceLocator.setPin(pinConfig)
    }

    fun DependencyHandler.tc(notation: String): Any {
        val plugin = ResourceLocator.getProject().getOurPlugin()
        val depDescription = DependencyDescriptor.create(notation) as DependencyDescriptor
        plugin.addDependency(depDescription)
        // todo: remove duplication
        val dep = create(depDescription.toDefaultDependencyNotation()) as ModuleDependency
        dep.artifact {
            it.name = depDescription.artifactDescriptor.name
            it.type = depDescription.artifactDescriptor.extension
        }
        return dep
    }

    private fun Project.getOurPlugin() = plugins.getPlugin("com.github.jk1.tcdeps") as TeamCityDependenciesPlugin
}