package com.github.jk1.tcdeps

import org.gradle.api.InvalidUserDataException


class ArtifactVersion {

    final def version;
    final def changing;

    public ArtifactVersion(version){
        if (version == null || version.isEmpty()) {
            throw new InvalidUserDataException("version should not be empty")
        }
        this.version = version
        this.changing = ['lastFinished','lastPinned','lastSuccessful','sameChainOrLastFinished'].contains(version)
    }
}
