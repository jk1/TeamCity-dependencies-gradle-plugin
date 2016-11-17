package com.github.jk1.tcdeps.model

import org.gradle.api.InvalidUserDataException

class ArtifactDescriptor {

    final String rawPath;
    final String path;
    final String name;
    final String extension;

    ArtifactDescriptor(String rawPath) {
        if (rawPath == null || rawPath.isEmpty()){
            throw new InvalidUserDataException("Artifact path may not be empty, please set at least a filename as artifact path")
        }
        this.rawPath = rawPath
        def lastDotIndex = rawPath.lastIndexOf('.')
        name = rawPath[0..lastDotIndex - 1]
        extension = rawPath[lastDotIndex + 1..rawPath.size() - 1]
    }

    boolean hasPath() {
        return path != null
    }

    @Override
    String toString() {
        "Artifact:[rawPath=$rawPath, name=$name, extension=$extension, path=$path]"
    }
}
