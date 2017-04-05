package com.github.jk1.tcdeps

import com.github.jk1.tcdeps.model.DependencyDescriptor
import com.github.jk1.tcdeps.repository.PinConfiguration
import com.github.jk1.tcdeps.util.ResourceLocator
import org.gradle.api.GradleException
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.IvyArtifactRepository

object KotlinScriptDslAdapter {
    fun RepositoryHandler.teamcityServer(action: IvyArtifactRepository.() -> Unit) :IvyArtifactRepository {
        val project = ResourceLocator.getProject()
        val plugin = project.plugins.getPlugin("com.github.jk1.tcdeps") as TeamCityDependenciesPlugin

        val repositories = project.repositories

        val oldRepo = repositories.findByName("TeamCity") as IvyArtifactRepository?
        val repo = plugin.createTeamCityRepository(project)
        action(repo)

        if(repo.url == null) {
            throw GradleException("Teamcity repository url shouldn't be null.")
        }

        ResourceLocator.getConfig().url = repo.url.toString()
        if (oldRepo != null) {
            project.logger.warn("Project $project already has TeamCity server ${oldRepo.url}, overriding with ${repo.url}")
            repositories.remove(oldRepo)
        }

        val tcUrl : String = repo.url!!.toString()
        val normalizeTcUrl : String = if (tcUrl[tcUrl.length - 1] == '/') tcUrl else tcUrl + "/"

        if(repo.credentials != null) {
            repo.setUrl("${normalizeTcUrl}httpAuth/repository/download")
        } else {
            repo.setUrl("${normalizeTcUrl}guestAuth/repository/download")
        }

        repositories.add(repo)
        return repo

    }

    fun IvyArtifactRepository.pin(action: PinConfiguration.() -> Unit){
        val pinConfig = ResourceLocator.getConfig()
        action(pinConfig)
        pinConfig.pinEnabled = true
        ResourceLocator.setPin(pinConfig)
    }

    fun DependencyHandler.tc(notation: String) : Any {
        val project = ResourceLocator.getProject()
        val plugin = project.plugins.getPlugin("com.github.jk1.tcdeps") as TeamCityDependenciesPlugin
        val depDescription = DependencyDescriptor.create(notation) as DependencyDescriptor
        plugin.addDependency(depDescription)
        val dep = create(depDescription.toDefaultDependencyNotation()) as ModuleDependency
        dep.artifact {
            it.name = depDescription.artifactDescriptor.name
            it.type = depDescription.artifactDescriptor.extension
        }
        return dep
    }
}