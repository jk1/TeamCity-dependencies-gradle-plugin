package com.github.jk1.tcdeps.client

import com.github.jk1.tcdeps.model.BuildLocator

class RequestBuilder {

    RestRequest request = new RestRequest()

    RequestBuilder(Closure closure) {
        closure.delegate = this                          // Set delegate of closure to this builder
        closure.resolveStrategy = Closure.DELEGATE_ONLY  // Only use this builder as the closure delegate
        closure()
    }

    void baseUrl(String base) {
        request.baseUrl = base && base.endsWith("/") ? base[0..-2] : base // remove trailing slash, if any
    }

    void login(String login) {
        request.authentication.login = login
    }

    void password(String password) {
        request.authentication.password = password
    }

    void locator(BuildLocator locator) {
        request.locator = locator
    }

    void body(String body) {
        request.body = body
    }

    void action(Closure closure){
        request.uriPath = closure
    }

    static def PIN = { BuildLocator locator ->
        "/httpAuth/app/rest/builds/$locator/pin"
    }

    static def GET_BUILD_NUMBER = { BuildLocator locator ->
        "/guestAuth/app/rest/builds/$locator/number"
    }
}
