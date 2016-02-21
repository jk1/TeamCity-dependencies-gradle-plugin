package com.github.jk1.tcdeps.client

import com.github.jk1.tcdeps.model.BuildLocator
import org.apache.http.HttpStatus
import spock.lang.Specification

class RestClientSpec extends Specification {

    private static final String kotlin_build_numbers = '^[0-9.\\-]*(beta|dev|rc)*[0-9.\\-]*$'

    def "Rest client should be able to fetch get build numbers for various build locators"() {
        def client = new RestClient()
        def response

        when:
        response = client.get(new RequestBuilder({
            baseUrl 'https://teamcity.jetbrains.com/'
            locator buildLocator
            action GET_BUILD_NUMBER
        }).request)

        then:
        response.code == responseCode
        response.body.matches(kotlin_build_numbers) // we expect a build number

        where:
        buildLocator                                             | responseCode
        new BuildLocator(buildTypeId: 'bt345')                   | HttpStatus.SC_OK
        new BuildLocator(buildTypeId: 'bt345', successful: true) | HttpStatus.SC_OK
        new BuildLocator(buildTypeId: 'bt345', pinned: true)     | HttpStatus.SC_OK
        new BuildLocator(buildTypeId: 'bt345', tag: 'bootstrap') | HttpStatus.SC_OK
    }

    def "Rest client should be able to handle missing builds"() {
        def client = new RestClient()
        def response

        when:
        response = client.get(new RequestBuilder({
            baseUrl 'https://teamcity.jetbrains.com/'
            locator buildLocator
            action GET_BUILD_NUMBER
        }).request)

        then:
        response.code == responseCode
        !response.body.isEmpty()

        where:
        buildLocator                                      | responseCode
        new BuildLocator(buildTypeId: 'bt1')              | HttpStatus.SC_NOT_FOUND
        new BuildLocator(buildTypeId: 'bt345', tag: '42') | HttpStatus.SC_NOT_FOUND
    }

    def "basic authentication should be supported"() {
        def client = new RestClient()
        def response

        when:
        response = client.get(new RequestBuilder({
            baseUrl 'https://teamcity.jetbrains.com/'
            locator new BuildLocator(buildTypeId: 'bt345')
            action GET_BUILD_NUMBER
            login 'guest'
            password 'guest'
        }).request)

        then:
        response.code == HttpStatus.SC_OK
        response.body.matches(kotlin_build_numbers) // we expect a build number
    }
}
