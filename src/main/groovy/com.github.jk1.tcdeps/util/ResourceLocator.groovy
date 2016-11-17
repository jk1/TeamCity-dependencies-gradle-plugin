package com.github.jk1.tcdeps.util

import com.github.jk1.tcdeps.client.RestClient

import org.gradle.api.GradleException
import org.gradle.api.Project

/**
 * Tiny IoC implementation for testability's sake
 */
class ResourceLocator {

    static ThreadLocal<Project> project = new ThreadLocal<>()

    static PropertyFileCache propertyCache = new PropertyFileCache()

    static RestClient restClient = new RestClient()

    static LogFacade logger = new LogFacade()

    static void setContext(Project theProject) {
        project.set(theProject)
    }

    static void closeResourceLocator() {
        propertyCache.flush()
        // cleanup to avoid memory leaks in daemon mode
        project.remove()
    }

    static def getConfig() {
        def repo = project.get().repositories.findByName("TeamCity")
        if (repo ==  null) {
            throw new GradleException("TeamCity repository is not defined for project ${project.get().name}")
        } else {
            return repo.pinConfig
        }
    }

    static def getProject() {
        return project.get()
    }
}
