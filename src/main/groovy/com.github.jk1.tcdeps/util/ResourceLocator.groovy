package com.github.jk1.tcdeps.util

import com.github.jk1.tcdeps.PluginConfiguration
import com.github.jk1.tcdeps.client.RestClient
import org.gradle.api.Project

/**
 * Tiny IoC implementation for testability's sake
 */
class ResourceLocator {

    static def ThreadLocal<Project> project = new ThreadLocal<>()

    static def ThreadLocal<PluginConfiguration> config = new ThreadLocal<>()

    static def PropertyFileCache propertyCache = new PropertyFileCache()

    static def RestClient restClient = new RestClient()

    static def LogFacade logger = new LogFacade()

    static void setContext(Project theProject) {
        project.set(theProject)
        config.set(theProject.teamcityServer)
    }

    static void closeResourceLocator() {
        propertyCache.flush()
        // cleanup to avoid memory leaks in daemon mode
        project.remove()
        config.remove()
    }

    static def getConfig() {
        return config.get()
    }

    static def getProject() {
        return project.get()
    }
}
