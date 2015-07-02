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

    static def PropertyFileCache propertyCache

    static def RestClient restClient

    static def LogFacade logger

    static void initResourceLocator(Project theProject) {
        if (project == null) {
            restClient = new RestClient()
            logger = new LogFacade()
            propertyCache = new PropertyFileCache(theProject.gradle)
        }
    }

    static void setContext(Project theProject) {
        project = theProject
        config = theProject.teamcityServer
    }

    static void closeResourceLocator() {
        if (propertyCache) {
            propertyCache.flush()
        }
        // cleanup to avoid memory leaks in daemon mode
        propertyCache = null
        project = null
        config = null
        restClient = null
        logger = null
    }
}
