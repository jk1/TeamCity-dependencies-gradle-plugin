package com.github.jk1.tcdeps.client

import groovy.transform.Canonical
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.auth.AuthScope
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils

class RestClient {

    def Response get(Closure closure){
        return get(new RequestBuilder(closure).request)
    }

    def Response get(RestRequest resource) {
        DefaultHttpClient client = new DefaultHttpClient()
        HttpGet request = new HttpGet(resource.toString())
        authenticate(client, resource.authentication)
        return readResponse(client.execute(request))
    }

    def Response put(Closure closure){
        return put(new RequestBuilder(closure).request)
    }

    def Response put(RestRequest resource) {
        DefaultHttpClient client = new DefaultHttpClient()
        HttpPut request = new HttpPut(resource.toString())
        authenticate(client, resource.authentication)
        StringEntity input = new StringEntity(resource.body);
        input.setContentType("text/html");
        request.setEntity(input);
        return readResponse(client.execute(request))
    }

   private def void authenticate(DefaultHttpClient client, Authentication auth) {
        if (auth.isRequired()) {
            client.getCredentialsProvider().setCredentials(AuthScope.ANY, auth.credentials)
        }
    }

    private def Response readResponse(HttpResponse response){
        return new Response(
                code: response.getStatusLine().getStatusCode(),
                body: EntityUtils.toString(response.getEntity()))
    }


    @Canonical
    static class Response {
        def int code = -1  // non-http error, e.g. TLS
        def String body = "No response recorded. Rerun with --stacktrace to see an exception."

        public isOk(){
            return HttpStatus.SC_OK == code
        }
    }
}
