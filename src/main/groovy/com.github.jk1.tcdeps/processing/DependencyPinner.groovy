package com.github.jk1.tcdeps.processing

import com.github.jk1.tcdeps.client.RestClient
import com.github.jk1.tcdeps.model.BuildLocator
import com.github.jk1.tcdeps.model.DependencyDescriptor
import org.gradle.api.GradleException

import static com.github.jk1.tcdeps.util.ResourceLocator.*

/**
 * Pin: PUT http://teamcity:8111/httpAuth/app/rest/builds/buildType:%s,number:%s/pin/
 * (the text in the request data is added as a comment for the action)
 *
 */
class DependencyPinner implements DependencyProcessor {

    @Override
    void process() {
        if (!dependencies.isEmpty()) {
            config.setDefaultMessage("Pinned when building dependent build $project.name $project.version")
            if (config.pinEnabled) {
                dependencies.findAll { shouldPin(it) }.unique().each {
                    def buildId = resolveBuildId(it)
                    if (buildId) {
                        pinBuild(buildId, it)
                        if (config.tag){
                            tagBuild(buildId, it)
                        }
                    }
                }
            } else {
                logger.debug("Dependency pinning is disabled")
            }
        }
    }

    /**
     * Dependencies with dynamic versions and explicit excludes should not be pinned or tagged
     */
    private def shouldPin(DependencyDescriptor dep){
      return !dep.version.changing && !config.excludes.contains(dep.buildTypeId)
    }

    private def pinBuild(String buildId, DependencyDescriptor dependency) {
        BuildLocator buildLocator = dependency.version.buildLocator
        buildLocator.buildTypeId = dependency.buildTypeId
        buildLocator.id = buildId
        logger.debug("Pinning the build: $buildLocator")
        try {
            def response = restClient.put {
                baseUrl config.url
                locator buildLocator
                action PIN
                body config.message
                login credentials?.username
                password credentials?.password
            }
            assertResponse(response, buildLocator)
        } catch (Exception e) {
            handleException(e, buildLocator)
        }
    }

    private def tagBuild(String buildId, DependencyDescriptor dependency) {
        BuildLocator buildLocator = dependency.version.buildLocator
        buildLocator.buildTypeId = dependency.buildTypeId
        buildLocator.id = buildId
        logger.debug("Tagging the build: $buildLocator")
        try {
            def response = restClient.post {
                baseUrl config.url
                locator buildLocator
                action TAG
                body config.tag
                login credentials?.username
                password credentials?.password
            }
            assertResponse(response, buildLocator)
        } catch (Exception e) {
            handleException(e, buildLocator)
        }
    }

    private def resolveBuildId(DependencyDescriptor dependency) {
        BuildLocator buildLocator = dependency.version.buildLocator
        buildLocator.buildTypeId = dependency.buildTypeId
        buildLocator.noFilter = true
        try {
            def response = restClient.get {
                baseUrl config.url
                locator buildLocator
                action GET_BUILD_ID
                body config.tag
                login credentials?.username
                password credentials?.password
            }
            assertResponse(response, buildLocator)
            return response.body
        } catch (Exception e) {
            handleException(e, buildLocator)
            return null
        }
    }

    private def handleException(Exception e, BuildLocator buildLocator) {
        if (e instanceof GradleException) {
            throw e
        }
        String message = "Unable to pin/tag build: $buildLocator"
        if (config.stopBuildOnFail) {
            throw new GradleException(message, e)
        } else {
            logger.warn(message, e)
        }
    }

    private def assertResponse(RestClient.Response response, BuildLocator buildLocator) {
        if (response && !response.isOk()) {
            String message = "Unable to pin/tag build: $buildLocator. Server response: HTTP $response.code \n $response.body"
            if (config.stopBuildOnFail) {
                throw new GradleException(message)
            } else {
                logger.warn(message)
            }
        }
    }
}
