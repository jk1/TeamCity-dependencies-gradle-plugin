package com.github.jk1.tcdeps.client

import com.github.jk1.tcdeps.model.BuildLocator
import groovy.transform.Canonical


@Canonical
class RestRequest {

    String baseUrl
    Closure uriPath
    BuildLocator locator

    Authentication authentication = new Authentication()
    String body

    String toString(){
        if (!baseUrl || !uriPath || !locator){
            throw new IllegalArgumentException("Base url, path and locator should be specified")
        }
        "$baseUrl${uriPath.call(locator, authentication.isRequired())}"
    }
}



