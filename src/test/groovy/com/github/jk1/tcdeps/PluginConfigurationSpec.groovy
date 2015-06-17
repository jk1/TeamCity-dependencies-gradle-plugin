package com.github.jk1.tcdeps

import org.gradle.api.InvalidUserDataException
import spock.lang.Specification


class PluginConfigurationSpec extends Specification {

    def "empty configuration should be considered invalid"() {
        def config = new PluginConfiguration()

        when:
        config.assertConfigured()

        then:
        thrown(InvalidUserDataException)
    }

    def "server URL is enough to form a minimum configuration"() {
        def config = new PluginConfiguration()

        when:
        config.url = 'http://teamcity.com'

        then:
        config.assertConfigured()
    }

    def "server URL should be a valid URL"() {
        def config = new PluginConfiguration()

        when:
        config.url = url
        config.assertConfigured()

        then:
        thrown(InvalidUserDataException)

        where:
        url << ['', null, '/relative/url', 'something_weird$%^&']
    }

    def "pinning the build requires login and password to be set"() {
        def config = new PluginConfiguration()

        when:
        config.url = 'http://teamcity.com'
        config.pin {
            username = 'login'
            password = 'password'
        }

        then:
        config.assertConfigured()
    }

    def "If there are no login or password, then validation error should indicate that"() {
        def config = new PluginConfiguration()

        when:
        config.url = 'http://teamcity.com'
        config.pin {
            username = loginValue
            password = passwordValue
        }
        config.assertConfigured()

        then:
        thrown(InvalidUserDataException)

        where:
        loginValue | passwordValue
        null       | null
        'login'    | null
        ''         | 'password'
        'login'    | ''
        null       | 'password'
    }

}
