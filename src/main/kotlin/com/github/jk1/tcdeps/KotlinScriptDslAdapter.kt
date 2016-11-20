package com.github.jk1.tcdeps

import com.github.jk1.tcdeps.repository.PinConfiguration
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.IvyArtifactRepository

object KotlinScriptDslAdapter {

    fun RepositoryHandler.teamcityServer(action: IvyArtifactRepository.() -> Unit){

    }

    fun IvyArtifactRepository.pin(action: PinConfiguration.() -> Unit){

    }

    fun DependencyHandler.tc(notation: String){

    }
}