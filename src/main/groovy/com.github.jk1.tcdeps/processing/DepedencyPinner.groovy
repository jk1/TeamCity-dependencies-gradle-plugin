package com.github.jk1.tcdeps.processing

import com.github.jk1.tcdeps.util.LogFacade
import com.github.jk1.tcdeps.PluginConfiguration
import org.gradle.api.GradleException

/**
 * Pin: PUT http://teamcity:8111/httpAuth/app/rest/builds/buildType:%s,number:%s/pin/
 * (the text in the request data is added as a comment for the action)
 *
 * http://confluence.jetbrains.com/display/TW/REST+API#RESTAPI-BuildLocator
 */
class DepedencyPinner implements DependencyProcessor {

    private PluginConfiguration config

    @Override
    def process() {
        config = project.teamcityServer
        config.setDefaultMessage("Pinned when building dependent build $project.name $project.version")
        if (config.pinEnabled) {
            // do not pin changing modules
            dependencies.findAll { !it.version.changing }.collectAll {
                "$config.url/httpAuth/app/rest/builds/buildType:$it.buildTypeId,number:$it.version.version/pin"
            }.unique().each { pinBuild(it) }
        } else {
            LogFacade.debug("Dependency pinning is disabled")
        }
    }

    private def pinBuild(String url) {
       /* String response = "No response recorded. Rerun with --stacktrace to see an exception."
        try {
            LogFacade.debug("Pinning the build: $url")
            HttpURLConnection connection = url.toURL().openConnection()
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            String encoded = "$config.username:$config.password".bytes.encodeBase64().toString();
            connection.setRequestProperty("Authorization", "Basic $encoded");
            connection.outputStream.withWriter { Writer writer -> writer << config.message }
            response = connection.inputStream.withReader { Reader reader -> reader.text }
        } catch (Exception e) {
            String message = "Unable to pin build: $url. Server response: \n $response"
            if (config.stopBuildOnFail) {
                throw new GradleException(message, e)
            } else {
                LogFacade.warn(message, e)
            }
        }*/

        String response = "No response recorded. Rerun with --stacktrace to see an exception."
        LogFacade.debug("Pinning the build: $url")
        
    }
}
