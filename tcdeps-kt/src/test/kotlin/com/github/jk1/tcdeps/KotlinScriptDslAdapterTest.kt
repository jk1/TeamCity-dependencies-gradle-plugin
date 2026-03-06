package com.github.jk1.tcdeps

import com.github.jk1.tcdeps.KotlinScriptDslAdapter.pin
import com.github.jk1.tcdeps.KotlinScriptDslAdapter.teamcityServer
import com.github.jk1.tcdeps.util.ResourceLocator
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.IvyArtifactRepository
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.URI

class KotlinScriptDslAdapterTest {

    private lateinit var project: Project

    @BeforeEach
    fun setUp() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply("com.github.jk1.tcdeps")
    }

    @AfterEach
    fun tearDown() {
        ResourceLocator.closeResourceLocator()
    }

    @Test
    fun `teamcity repository should use guest auth urls when no username is available`() {
        project.repositories.teamcityServer {
            url = URI("http://teamcity")
        }

        val repo = project.repositories.findByName("TeamCity") as IvyArtifactRepository
        assertEquals(URI("http://teamcity/guestAuth/repository/download"), repo.url)
        assertNull(ResourceLocator.getCredentials())
    }

    @Test
    fun `teamcity repository should use http auth when credentials are provided`() {
        project.repositories.teamcityServer {
            url = URI("http://teamcity")
            credentials {
                it.username = "name"
                it.password = "secret"
            }
        }

        val repo = project.repositories.findByName("TeamCity") as IvyArtifactRepository
        assertEquals(URI("http://teamcity/httpAuth/repository/download"), repo.url)
        assertNotNull(ResourceLocator.getCredentials())
    }

    @Test
    fun `teamcity repository should support pin configuration`() {
        project.repositories.teamcityServer {
            url = URI("http://teamcity")
            credentials {
                it.username = "name"
                it.password = "secret"
            }
            pin {
                stopBuildOnFail = true
                message = "Pinned for MyCoolProject"
            }
        }

        val repo = project.repositories.findByName("TeamCity") as IvyArtifactRepository
        assertEquals("http://teamcity/httpAuth/repository/download", repo.url.toString())
        assertEquals("name", repo.credentials.username)
        assertEquals("secret", repo.credentials.password)
        val pinConfig = ResourceLocator.getConfig()
        assertTrue(pinConfig.pinEnabled)
        assertEquals("Pinned for MyCoolProject", pinConfig.message)
        assertTrue(pinConfig.stopBuildOnFail)
    }

    @Test
    fun `should maintain only one TeamCity server`() {
        project.repositories.teamcityServer {
            url = URI("http://teamcity1")
        }
        project.repositories.teamcityServer {
            url = URI("http://teamcity2")
        }

        val repos = project.repositories.filterIsInstance<IvyArtifactRepository>()
        assertEquals(1, repos.size)
        assertTrue(repos[0].url.toString().contains("teamcity2"))
    }
}
