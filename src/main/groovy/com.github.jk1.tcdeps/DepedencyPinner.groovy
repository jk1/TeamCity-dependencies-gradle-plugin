package com.github.jk1.tcdeps

import org.apache.xerces.impl.dv.util.Base64

/**
 * Pin: PUT http://teamcity:8111/httpAuth/app/rest/builds/buildType:%s,number:%s/pin/
 * (the text in the request data is added as a comment for the action)
 *
 * http://confluence.jetbrains.com/display/TW/REST+API#RESTAPI-BuildLocator
 */
class DepedencyPinner {

    def pin(String url, String username, String password) {
        HttpURLConnection connection = url.toURL().openConnection()
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");
        connection.outputStream.withWriter { Writer writer ->
            writer << "Automatically pinned by ..."
        }
        String encoded = Base64.encode("$username:$password");
        connection.setRequestProperty("Authorization", "Basic $encoded");
        String response = connection.inputStream.withReader { Reader reader -> reader.text }

    }
}
