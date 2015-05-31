package com.github.jk1.tcdeps.model

import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project

class VersionPlaceholders {
  def staticPlaceholders =  ['lastFinished':'',
                             'lastPinned': ',pinned:true',
                             'lastSuccessful':',status:SUCCESS',
                             'sameChainOrLastFinished':'']

  def getAt(String key) {
    if (staticPlaceholders.containsKey(key)) {
      return staticPlaceholders[key]
    } else if (key.endsWith(".tcbuildtag")) {
      def tag = key.subSequence(0, key.lastIndexOf(".tcbuildtag"))
      return ",tags:$tag"
    } else {
      return null
    }
  }

  def containsKey(String key) {
    return staticPlaceholders.containsKey(key) || key.endsWith(".tcbuildtag")
  }
}

class ArtifactVersion {

    def versionPlaceholders = new VersionPlaceholders()

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
