package com.github.jk1.tcdeps.processing

import com.github.jk1.tcdeps.model.BuildLocator
import com.github.jk1.tcdeps.model.DependencyDescriptor
import org.gradle.api.GradleException

import static com.github.jk1.tcdeps.util.ResourceLocator.*

/**
 * Pin: PUT http://teamcity:8111/httpAuth/app/rest/builds/buildType:%s,number:%s/pin/
 * (the text in the request data is added as a comment for the action)
 *
 */
class DepedencyPinner implements DependencyProcessor {

    @Override
    def process() {
        config.setDefaultMessage("Pinned when building dependent build $project.name $project.version")
        if (config.pinEnabled) {
            // do not pin changing modules
            dependencies.findAll { !it.version.changing }.unique().each { pinBuild(it) }
        } else {
            logger.debug("Dependency pinning is disabled")
        }
    }

    private def pinBuild(DependencyDescriptor dependency) {
        def BuildLocator buildLocator = dependency.version.buildLocator
        buildLocator.buildTypeId = dependency.buildTypeId
        buildLocator.branch = dependency.branch
        logger.debug("Pinning the build: $buildLocator")
        // todo: rewrite this to avoid boilerplate
        def response
        try {
            response = restClient.put {
                baseUrl config.url
                locator buildLocator
                action PIN
                body config.message
                login config.username
                password config.password
            }
        } catch (Exception e) {
            String message = "Unable to pin build: $buildLocator"
            if (config.stopBuildOnFail) {
                throw new GradleException(message, e)
            } else {
                logger.warn(message, e)
            }
        }
        if (response && !response.isOk()) {
            String message = "Unable to pin build: $buildLocator. Server response: HTTP $response.code \n $response.body"
            if (config.stopBuildOnFail) {
                throw new GradleException(message)
            } else {
                logger.warn(message)
            }
        }
    }
}
