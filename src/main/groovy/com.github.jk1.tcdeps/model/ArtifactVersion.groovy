package com.github.jk1.tcdeps.model

import com.github.jk1.tcdeps.PluginConfiguration
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project


class ArtifactVersion {

    def versionPlaceholders = ['lastFinished':'',
                               'lastPinned': ',pinned:true',
                               'lastSuccessful':',status:SUCCESS',
                               'sameChainOrLastFinished':'']

    def version;
    def changing;

    public ArtifactVersion(String version){
        if (version == null || version.isEmpty()) {
            throw new InvalidUserDataException("version should not be empty")
        }
        this.version = version
        this.changing = versionPlaceholders.containsKey(version)
    }

    def resolve(Project project, String btid){
        // version is defined - nothing to resolve
    }

    @Override
    String toString() {
        "Version:[version=$version, changing=$changing]"
    }
}
