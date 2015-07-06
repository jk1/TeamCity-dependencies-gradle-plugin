package com.github.jk1.tcdeps.util

import org.gradle.api.invocation.Gradle
import spock.lang.Specification


class PropertyFileCacheSpec extends Specification {

    private File temp = new File(System.getProperty("java.io.tmpdir"), 'gradlePluginTestCache')

    def cleanup() { temp.deleteDir() }

    def "cache should be able to persist properties"() {
        //gradle.getGradleUserHomeDir() >> temp todo!

        when:
        def cache = new PropertyFileCache()
        cache.store('key', 'value')
        cache.flush()

        then:
        def otherCache = new PropertyFileCache()
        otherCache.load('key').equals('value')
    }
}
