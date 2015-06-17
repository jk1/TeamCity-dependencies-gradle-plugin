package com.github.jk1.tcdeps.client

import com.github.jk1.tcdeps.model.BuildLocator

class RequestBuilder {

    RestRequest request

    RequestBuilder(Closure closure) {
        closure.delegate = this                          // Set delegate of closure to this builder
        closure.resolveStrategy = Closure.DELEGATE_ONLY  // Only use this builder as the closure delegate
        closure()
    }

    void baseUrl(String base) {
        request.baseUrl = base
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
}
