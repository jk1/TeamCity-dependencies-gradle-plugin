package com.github.jk1.tcdeps.client

import com.github.jk1.tcdeps.model.BuildLocator
import spock.lang.Specification

class RequestBuilderSpec extends Specification {

    def "request builder should produce anonymous pin url by default"() {
        def builder = new RequestBuilder({
            baseUrl 'http://server.org'
            locator new BuildLocator(buildTypeId: 'bt1', number: '1')
            action PIN
        })

        expect:
        builder.request.toString().equals("http://server.org/guestAuth/app/rest/builds/buildType:bt1,number:1/pin")
    }

    def "request builder should produce authneticated pin url if login and password are set"() {
        def builder = new RequestBuilder({
            baseUrl 'http://server.org'
            locator new BuildLocator(buildTypeId: 'bt1', number: '1')
            action PIN
            login "login"
            password "password"
        })

        expect:
        builder.request.toString().equals("http://server.org/httpAuth/app/rest/builds/buildType:bt1,number:1/pin")
    }

    def "request builder should produce valid artifact version resolution url"() {
        def builder = new RequestBuilder({
            baseUrl 'http://server.org'
            action GET_BUILD_NUMBER
            locator new BuildLocator(buildTypeId: 'bt1')
        })

        expect:
        builder.request.toString().equals('http://server.org/guestAuth/app/rest/builds/buildType:bt1/number')
    }

    def "request builder should support authentication"() {
        def builder = new RequestBuilder({
            login userLogin
            password userPassword
        })

        expect:
        builder.request.authentication.isRequired().equals(required)

        where:
        userLogin | userPassword | required
        null      | null         | false
        'login'   | null         | false
        null      | 'password'   | false
        'login'   | 'password'   | true
    }

    def "request builder should fail if some request components are missing"() {
        def builder = new RequestBuilder({
            baseUrl base
            locator buildLocator
            action path
        })

        when:
        builder.request.toString()

        then:
        thrown(IllegalArgumentException)

        where:
        base                | path | buildLocator
        "http://server.org" | {}   | null
        "http://server.org" | null | new BuildLocator()
        null                | {}   | new BuildLocator()
    }

}