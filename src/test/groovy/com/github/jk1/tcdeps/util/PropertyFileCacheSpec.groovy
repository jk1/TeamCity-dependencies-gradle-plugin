package com.github.jk1.tcdeps.util

import org.gradle.api.invocation.Gradle
import spock.lang.Specification


class PropertyFileCacheSpec extends Specification {

    private File temp = new File(System.getProperty("java.io.tmpdir"), 'gradlePluginTestCache')
    private Gradle gradle;

    def setup() {
        gradle = Mock(Gradle)
        gradle.getGradleVersion() >> '2.5'
    }

    def cleanup() { temp.deleteDir() }

    def "cache should be able to persist properties"() {
        gradle.getGradleUserHomeDir() >> temp

        when:
        def cache = new PropertyFileCache(gradle)
        cache.store('key', 'value')
        cache.flush()

        then:
        def otherCache = new PropertyFileCache(gradle)
        otherCache.load('key').equals('value')
    }
}
