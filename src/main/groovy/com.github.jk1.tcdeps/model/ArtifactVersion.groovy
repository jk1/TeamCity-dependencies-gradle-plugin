package com.github.jk1.tcdeps.model

import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project

class ArtifactVersion {

    def version;
    def changing;

    public ArtifactVersion(String version){
        if (version == null || version.isEmpty()) {
            throw new InvalidUserDataException("version should not be empty")
        }
        this.version = version
        this.changing = new ChangingModulePlaceholers().containsKey(version)
    }

    def resolve(Project project, String btid){
        // version is defined - nothing to resolve
    }

    @Override
    String toString() {
        "Version:[version=$version, changing=$changing]"
    }
}
