package com.github.jk1.tcdeps.client

import groovy.transform.Canonical

class RestClient {

    Response get(RestRequest resource){
        execute("GET", resource)
    }

    Response put(RestRequest resource){
        execute("PUT", resource)
    }

    Response execute(String method, RestRequest resource) {
        HttpURLConnection connection = resource.toUrl().toURL().openConnection()
        connection.setRequestMethod(method.toUpperCase())
        authenticate(connection, resource.authentication)
        writeRequest(connection, resource)
        return new Response(code: connection.getResponseCode(),
                body: connection.inputStream.withReader { Reader reader -> reader.text })

    }

    private void authenticate(HttpURLConnection connection, Authentication auth) {
        if (auth.isRequired()) {
            String encoded = "$auth.login:$auth.password".bytes.encodeBase64().toString();
            connection.setRequestProperty("Authorization", "Basic $encoded");
        }
    }

    private void writeRequest(HttpURLConnection connection, RestRequest resource) {
        if (resource.body) {
            connection.setDoOutput(true)
            connection.outputStream.withWriter { it << resource.body }
        }
    }

    @Canonical
    static class Response {
        int code
        String body
    }
}
