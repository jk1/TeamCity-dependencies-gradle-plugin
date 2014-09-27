package com.github.jk1.tcdeps

class ArtifactDescriptor {

    private String rawPath;
    private String path;
    private String name;
    private String extension;

    ArtifactDescriptor(String rawPath) {
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
        println(rawPath)
        println(path)
        println(name)
        println(extension)
    }

    String getRawPath() {
        return rawPath
    }

    boolean hasAdditionalPath() {
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
}
