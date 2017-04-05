package com.github.jk1.tcdeps.util

import com.github.jk1.tcdeps.client.RestClient
import com.github.jk1.tcdeps.repository.PinConfiguration
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

    static PinConfiguration pinConfiguration = new PinConfiguration()

    static void setContext(Project theProject) {
        project.set(theProject)
    }

    static void setPin(PinConfiguration pin) {
        pinConfiguration = pin
    }

    static void closeResourceLocator() {
        propertyCache.flush()
        // cleanup to avoid memory leaks in daemon mode
        project.remove()
    }

    static PinConfiguration getConfig() {
        def repo = project.get().repositories.findByName("TeamCity")
        if (repo ==  null) {
            throw new GradleException("TeamCity repository is not defined for project ${project.get().name}")
        } else {
            return pinConfiguration
        }
    }

    static Project getProject() {
        return project.get()
    }
}
