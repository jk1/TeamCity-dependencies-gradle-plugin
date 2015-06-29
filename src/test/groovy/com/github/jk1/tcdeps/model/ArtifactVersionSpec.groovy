package com.github.jk1.tcdeps.model

import spock.lang.Specification

class ArtifactVersionSpec extends Specification {

    def "version parser should result in correct build locator"() {
        when:
        ArtifactVersion version = new ArtifactVersion(versionValue)

        then:
        version.buildLocator.equals(locator)

        where:
        versionValue         | locator
        'lastFinished'       | new BuildLocator()
        'TagName.tcbuildtag' | new BuildLocator(tag: 'TagName')
        'lastSuccessful'     | new BuildLocator(successful: true)
        'lastPinned'         | new BuildLocator(pinned: true)
    }

    def "changing module version should require explicit resolution"() {
        when:
        ArtifactVersion version = new ArtifactVersion(versionValue)

        then:
        version.needsResolution == changing
        version.changing == changing

        where:
        versionValue         | changing
        'lastFinished'       | true
        'TagName.tcbuildtag' | true
        'lastSuccessful'     | true
        'lastPinned'         | true
        '1.0.0'              | false
    }
}
