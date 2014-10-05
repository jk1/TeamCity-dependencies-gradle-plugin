package com.github.jk1.tcdeps

import org.gradle.api.InvalidUserDataException


class DependencyDescriptor {

    final def ArtifactDescriptor artifactDescriptor;
    final def buildTypeId;
    final def version;

    public DependencyDescriptor(String buildTypeId, String version, String artifactPath) {
        if (buildTypeId == null || buildTypeId.isEmpty()) {
            throw new InvalidUserDataException("buildTypeId should not be empty")
        }
        if (buildTypeId == null || buildTypeId.isEmpty()) {
            throw new InvalidUserDataException("version should not be empty")
        }
        this.buildTypeId = buildTypeId;
        this.version = version;
        this.artifactDescriptor = new ArtifactDescriptor(artifactPath)
    }

    public DependencyDescriptor(String dependencyNotation) {
        if (dependencyNotation == null) {
            throw new InvalidUserDataException("Dependency cannot be empty")
        }
        String[] dependency = dependencyNotation.split(":")
        if (dependency.size() < 3 || dependency.any({ it.isEmpty() })) {
            throw new InvalidUserDataException(
                    "Invalid dependency notation format. Usage: 'buildTypeId:version:artifact'"
            )
        }
        this.buildTypeId = dependency[0]
        this.version = dependency[1]
        this.artifactDescriptor = new ArtifactDescriptor(dependency[2])
    }

    public DependencyDescriptor(Map dependency) {
        if (dependency == null) {
            throw new InvalidUserDataException("Dependency cannot be empty")
        }
        this.buildTypeId = dependency["buildTypeId"]
        this.version = dependency["version"]
        if (buildTypeId == null || buildTypeId.isEmpty()) {
            throw new InvalidUserDataException("buildTypeId should not be empty")
        }
        if (buildTypeId == null || buildTypeId.isEmpty()) {
            throw new InvalidUserDataException("version should not be empty")
        }
        this.artifactDescriptor = new ArtifactDescriptor(dependency["artifactPath"])
    }

    def toDependencyNotation() {
        return ["org:$buildTypeId:$version", { ->
            artifact {
                name = artifactDescriptor.name
                type = artifactDescriptor.extension
            }
        }]
    }
}
