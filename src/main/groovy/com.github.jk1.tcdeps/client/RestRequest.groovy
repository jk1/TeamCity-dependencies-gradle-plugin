package com.github.jk1.tcdeps.client

import com.github.jk1.tcdeps.model.BuildLocator
import groovy.transform.Canonical


@Canonical
class RestRequest {

    def String baseUrl
    def Closure uriPath
    def BuildLocator locator

    def Authentication authentication = new Authentication()
    def String body

    String toUrl(){
        baseUrl
    }
}

@Canonical
class Authentication{
    def String login
    def String password

    boolean isRequired() {
        login && password
    }
}

