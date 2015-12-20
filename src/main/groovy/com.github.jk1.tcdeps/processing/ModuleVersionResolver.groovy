package com.github.jk1.tcdeps.processing

import com.github.jk1.tcdeps.client.RestClient
import com.github.jk1.tcdeps.model.BuildLocator
import com.github.jk1.tcdeps.model.DependencyDescriptor
import org.gradle.api.GradleException

import static com.github.jk1.tcdeps.util.ResourceLocator.*

/**
 * Resolves changing module versions, e.g. lastPinned, against TeamCity feature branches.
 * It doesn't look like TeamCity's capable of customizing ivy.xml based on branch locator,
 * so we're trying to resolve exact build number beforehand to work this around
 */
class ModuleVersionResolver implements DependencyProcessor {

    @Override
    def addDependency(DependencyDescriptor dependency) {
        if (dependency.getVersion().needsResolution) {
            def BuildLocator buildLocator = dependency.version.buildLocator
            buildLocator.buildTypeId = dependency.buildTypeId
            buildLocator.branch = dependency.branch
            if (project.gradle.startParameter.offline) {
                // offline mode - get the latest version from the cache
                dependency.version.resolved(propertyCache.load(buildLocator.toString()))
                logger.info("Unable to resolve $dependency in offline mode, falling back to last cached version")
            } else {
                String resolvedVersion = doResolve(dependency)
                propertyCache.store(buildLocator.toString(), resolvedVersion)
                dependency.version.resolved(resolvedVersion)
            }
        }
    }

    private String doResolve(DependencyDescriptor dependency) {
        def BuildLocator buildLocator = dependency.version.buildLocator
        buildLocator.buildTypeId = dependency.buildTypeId
        buildLocator.branch = dependency.branch
        def response = getBuildNumberFromServer(buildLocator)
        if (response.isOk()) {
            logger.info("$dependency.version ($BuildLocator) has been resolved to a build #${response.body}")
            dependency.version.resolved(response.body)
        } else {
            String message = "Unable to resolve $dependency.version. \nServer response: \n $response"
            throw new GradleException(message)
        }
    }

    private RestClient.Response getBuildNumberFromServer(BuildLocator buildLocator) {
        try {
            return restClient.get {
                baseUrl config.url
                locator buildLocator
                action GET_BUILD_NUMBER
            }
        } catch (Exception e) {
            throw new GradleException("Failed to resolve $buildLocator", e)
        }
    }
}
