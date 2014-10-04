package com.github.jk1.tcdeps

import groovy.text.SimpleTemplateEngine
import org.gradle.api.GradleException

/**
 * Pin: PUT http://teamcity:8111/httpAuth/app/rest/builds/buildType:%s,number:%s/pin/
 * (the text in the request data is added as a comment for the action)
 *
 * http://confluence.jetbrains.com/display/TW/REST+API#RESTAPI-BuildLocator
 */
class DepedencyPinner implements DependencyProcessor {

    private template = new SimpleTemplateEngine().createTemplate(
            '$server/httpAuth/app/rest/builds/buildType:$buildTypeId,number:$version/pin')

    def process() {
        println("Pinning all builds")
        if (config.pinEnabled) {
            println("Pin is enabled, $pinCandidates deps")
            dependecies.collectAll {
                template.make(
                        'server': config.url,
                        'buildTypeId': it.buildTypeId,
                        'version': it.version
                ).toString()
            }.unique().each { pinBuild(it) }
        }
    }

    private def pinBuild(String url) {
        String response = "<no response recorded>"
        try {
            HttpURLConnection connection = url.toURL().openConnection()
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            String encoded = "$config.username:$config.password".bytes.encodeBase64().toString();
            println("Encoded $encoded")
            connection.setRequestProperty("Authorization", "Basic $encoded");
            connection.outputStream.withWriter { Writer writer -> writer << config.message }
            response = connection.inputStream.withReader { Reader reader -> reader.text }
        } catch (Exception e) {
            String message = "Unable to pin build: $url. Server response: \n $response"
            if (config.stopBuildOnFail) {
                throw new GradleException(message, e)
            } else {
                logger.warn(message, e)
            }
        }
    }
}
