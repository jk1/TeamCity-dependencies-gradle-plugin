package com.github.jk1.tcdeps

import org.gradle.api.InvalidUserDataException

class ArtifactDescriptor {

    private String rawPath;
    private String path;
    private String name;
    private String extension;

    ArtifactDescriptor(String rawPath) {
        if (rawPath == null || rawPath.isEmpty()){
            throw new InvalidUserDataException("Artifact path may not be empty, please set at least a filename as artifact path")
        }
        this.rawPath = rawPath
        def lastDotIndex = rawPath.lastIndexOf('.')
        def lastSeparatorIndex = rawPath.lastIndexOf("/")
        extension = rawPath[lastDotIndex + 1..rawPath.size() - 1]
        if (lastSeparatorIndex == -1) {
            name = rawPath[0..lastDotIndex - 1]
        } else {
            name = rawPath[lastSeparatorIndex + 1..lastDotIndex - 1]
            path = rawPath[0..lastSeparatorIndex]
        }
    }

    String getRawPath() {
        return rawPath
    }

    boolean hasPath() {
        return path != null
    }

    String getPath() {
        return path
    }

    String getName() {
        return name
    }

    String getExtension() {
        return extension
    }

    @Override
    String toString() {
        "ArtifactDescriptor:[rawPath=$rawPath, name=$name, extension=$extension, path=$path]"
    }
}
