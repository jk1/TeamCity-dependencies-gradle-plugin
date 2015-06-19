package com.github.jk1.tcdeps.model

import spock.lang.Specification


class BuildLocatorSpec extends Specification{

    def "build locator should require at least build type id"() {
        def locator = new BuildLocator()

        when:
        locator.toString()

        then:
        thrown(IllegalArgumentException)
    }
}
