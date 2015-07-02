package com.github.jk1.tcdeps.model

import org.gradle.api.InvalidUserDataException

class ArtifactVersion {

    def String version
    def BuildLocator buildLocator
    def boolean needsResolution = false
    def boolean changing = false

    // todo: shouldn't it a be full featured parser instead?
    private def placeholders = ['lastFinished'           : { return new BuildLocator() },
                                'sameChainOrLastFinished': { return new BuildLocator() },
                                'lastPinned'             : { return new BuildLocator(pinned: true) },
                                'lastSuccessful'         : { return new BuildLocator(successful: true) }]

    public ArtifactVersion(String version) {
        if (version == null || version.isEmpty()) {
            throw new InvalidUserDataException("version should not be empty")
        }
        this.version = version
        if (placeholders.containsKey(version)) {
            needsResolution = true
            changing = true
            buildLocator = placeholders[version]()
        } else if (version.endsWith('.tcbuildtag')) {
            needsResolution = true
            changing = true
            buildLocator = new BuildLocator(tag: version - '.tcbuildtag')
        } else {
            buildLocator = new BuildLocator(number: version)
        }
    }

    def resolved(version) {
        this.needsResolution = false
        this.version = version
    }


    @Override
    String toString() {
        "Version:[version=$version, resolved=${!needsResolution}]"
    }
}
