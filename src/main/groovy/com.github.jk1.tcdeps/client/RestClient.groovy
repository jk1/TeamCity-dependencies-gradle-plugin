package com.github.jk1.tcdeps.client

import groovy.transform.Canonical

class RestClient {

    Response get(Closure closure) {
        return get(new RequestBuilder(closure).request)
    }

    Response get(RestRequest resource) {
        return execute("GET", resource)
    }

    Response put(Closure closure) {
        return put(new RequestBuilder(closure).request)
    }

    Response put(RestRequest resource) {
        return execute("PUT", resource)
    }

    Response post(Closure closure) {
        return post(new RequestBuilder(closure).request)
    }

    Response post(RestRequest resource) {
        return execute("POST", resource)
    }

    Response delete(Closure closure) {
        return delete(new RequestBuilder(closure).request)
    }

    Response delete(RestRequest resource) {
        return execute("DELETE", resource)
    }

    private Response execute(String method, RestRequest resource) {
        HttpURLConnection connection = resource.toString().toURL().openConnection()
        connection.setRequestMethod(method.toUpperCase())
        connection.setRequestProperty("Content-Type", "text/plain")
        authenticate(connection, resource.authentication)
        writeRequest(connection, resource)
        return new Response(code: connection.getResponseCode(), body: readResponse(connection))
    }

    private void authenticate(HttpURLConnection connection, Authentication auth) {
        if (auth.isRequired()) {
            connection.setRequestProperty("Authorization", auth.asHttpHeader())
        }
    }

    private void writeRequest(HttpURLConnection connection, RestRequest resource) {
        if (resource.body) {
            connection.setDoOutput(true)
            connection.outputStream.withWriter { it << resource.body }
        }
    }

    private String readResponse(HttpURLConnection connection) {
        return (connection.getResponseCode() < 400 ?
                connection.inputStream :
                connection.getErrorStream()).withReader { Reader reader -> reader.text }
    }

    @Canonical
    static class Response {
        int code = -1  // non-http error, e.g. TLS
        String body = "No response recorded. Rerun with --stacktrace to see an exception."

        boolean isOk() {
            return (200..<300).contains(code)
        }
    }
}
