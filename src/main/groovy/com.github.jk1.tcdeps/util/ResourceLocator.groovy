package com.github.jk1.tcdeps.util

import com.github.jk1.tcdeps.PluginConfiguration
import com.github.jk1.tcdeps.client.RestClient
import org.gradle.api.Project

/**
 * Tiny IoC implementation for testability's sake
 */
class ResourceLocator {

    static def Project project

    static def PluginConfiguration config

    static def RestClient restClient = new RestClient()

    static def LogFacade logger = new LogFacade()

    static setProject(Project theProject) {
        project = theProject
        project.gradle.buildFinished {
            // cleanup to avoid memory leaks in daemon mode
            project = null
            config = null
            restClient = null
            logger = null
        }
    }
}
