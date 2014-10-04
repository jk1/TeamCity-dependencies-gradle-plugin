package com.github.jk1.tcdeps

import groovy.text.SimpleTemplateEngine

/**
 * Pin: PUT http://teamcity:8111/httpAuth/app/rest/builds/buildType:%s,number:%s/pin/
 * (the text in the request data is added as a comment for the action)
 *
 * http://confluence.jetbrains.com/display/TW/REST+API#RESTAPI-BuildLocator
 */
class DepedencyPinner {

    private template = new SimpleTemplateEngine().createTemplate(
            '$server/httpAuth/app/rest/builds/buildType:$buildTypeId,number:$version/pin')

    private ConfigurationExtension config;
    private Set<DependencyDescriptor> pinCandidates = new HashSet<>()

    def setConfig(ConfigurationExtension config) {
        this.config = config
    }

    def addDependency(DependencyDescriptor dependency) {
        pinCandidates.add(dependency)
    }

    def pinAllBuilds() {
        println("Pinning all builds")
        if (config.pinEnabled) {
            println("Pin is enabled, $pinCandidates deps")
            pinCandidates.collectAll {
                template.make(
                        'server': config.url,
                        'buildTypeId': it.buildTypeId,
                        'version': it.version
                ).toString()
            }.unique().each { pinBuild(it) }
        }
    }

    private def pinBuild(String url) {
        println("Pinning $url")
        HttpURLConnection connection = url.toURL().openConnection()
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");
        println("Credentials $config.username:$config.password")
        String encoded = "$config.username:$config.password".bytes.encodeBase64().toString();
        println("Encoded $encoded")
        connection.setRequestProperty("Authorization", "Basic $encoded");
        //connection.outputStream.withWriter { Writer writer -> writer << config.message }
        String response = connection.inputStream.withReader { Reader reader -> reader.text }
        println(response)
    }
}
