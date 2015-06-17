package com.github.jk1.tcdeps.model

import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project

class ArtifactVersion {

    def version
    def needsResolution = false
    def buildLocator

    private def placeholders = ['lastFinished'           : { return new BuildLocator() },
                                'sameChainOrLastFinished': { return new BuildLocator() },
                                'lastPinned'             : { return new BuildLocator(pinned: true) },
                                'lastSuccessful'         : { return new BuildLocator(successful: true) }]

    public ArtifactVersion(String version){
        if (version == null || version.isEmpty()) {
            throw new InvalidUserDataException("version should not be empty")
        }
        this.version = version
        if (placeholders.containsKey(version)){
            needsResolution = true
            buildLocator = placeholders[version]

        } else if (version.endsWith('.tcbuildtag')){
            needsResolution = true
            buildLocator = new BuildLocator(tag: version - '.tcbuildtag')
        }
    }


    @Override
    String toString() {
        "Version:[version=$version, resolved=${!needsResolution}]"
    }
}
