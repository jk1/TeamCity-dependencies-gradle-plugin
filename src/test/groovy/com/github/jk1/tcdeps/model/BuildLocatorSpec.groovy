package com.github.jk1.tcdeps.model

import spock.lang.Specification

/**
 * https://confluence.jetbrains.com/display/TCD8/REST+API#RESTAPI-BuildLocator
 */
class BuildLocatorSpec extends Specification {

    def "build locator should require at least build type id"() {
        def locator = new BuildLocator()

        when:
        locator.toString()

        then:
        thrown(IllegalArgumentException)
    }

    def "build locator should be serializable into valid path component"() {
        def locator = new BuildLocator(buildTypeId: btId, pinned: pin, successful: success,
                branch: vcsBranch, tag: tcTag, number: buildNumber)

        expect:
        locator.toString().equals(result)

        where:
        btId  | pin   | success | vcsBranch | tcTag | buildNumber | result
        'bt1' | false | false   | null      | null  | null        | 'buildType:bt1'
        'bt1' | true  | false   | null      | null  | null        | 'buildType:bt1,pinned:true'
        'bt1' | true  | true    | null      | null  | null        | 'buildType:bt1,pinned:true,status:SUCCESS'
        'bt1' | false | false   | 'master'  | null  | null        | 'buildType:bt1,branch:master'
        'bt1' | false | false   | 'master'  | 'ok'  | null        | 'buildType:bt1,branch:master,tags:ok'
        'bt1' | false | false   | null      | null  | '1.0.15'    | 'buildType:bt1,number:1.0.15'
        'bt1' | true  | true    | 'dev'     | 'ok'  | '1'         | 'buildType:bt1,branch:dev,tags:ok,pinned:true,status:SUCCESS,number:1'
    }

    def "build locator should apply URL encoding where necessary"() {
        def locator = new BuildLocator(buildTypeId: btId, branch: vcsBranch, tag: tcTag, number: buildNumber)

        expect:
        locator.toString().equals(result)

        where:
        btId  | vcsBranch          | tcTag      | buildNumber  | result
        'bt/' | null               | null       | null         | 'buildType:bt%2F'
        'bt1' | 'ref/heads/branch' | null       | null         | 'buildType:bt1,branch:ref%2Fheads%2Fbranch'
        'bt1' | null               | 'omg!wtf?' | null         | 'buildType:bt1,tags:omg%21wtf%3F'
        'bt1' | null               | null       | '3.14/15%27' | 'buildType:bt1,number:3.14%2F15%2527'
    }
}
