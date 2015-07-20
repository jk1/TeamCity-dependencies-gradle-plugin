package com.github.jk1.tcdeps.client

import spock.lang.Specification

class RestResponseSpec extends Specification {

    def "rest response should be able to store response code and it's body"() {
        def response = new RestClient.Response()

        when:
        response.code = 200
        response.body = "Response body"

        then:
        response.code == 200
        response.body == "Response body"
    }

    def "with no explicit result set rest response assumes transport error"() {
        def response = new RestClient.Response()

        expect:
        response.code == -1
        !response.isOk()
    }

    def "2XX http response codes stands for successful result"() {
        def response = new RestClient.Response()

        when:
        response.code = code

        then:
        response.isOk() == result

        where:
        code | result
        200  | true
        204  | true
        302  | false
        404  | false
        500  | false
    }
}
