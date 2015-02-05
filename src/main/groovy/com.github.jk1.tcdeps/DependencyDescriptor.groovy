package com.github.jk1.tcdeps

import org.gradle.api.InvalidUserDataException


class DependencyDescriptor {

    final def buildTypeId;
    final def artifactDescriptor;
    final def version;

    private DependencyDescriptor(
            String buildTypeId, ArtifactVersion version, ArtifactDescriptor artifactDescriptor) {
        this.buildTypeId = buildTypeId;
        this.version = version;
        this.artifactDescriptor = artifactDescriptor
    }

    static def create(String buildTypeId, String version, String artifactPath) {
        if (buildTypeId == null || buildTypeId.isEmpty()) {
            throw new InvalidUserDataException("buildTypeId should not be empty")
        }
        return new DependencyDescriptor(buildTypeId,
                new ArtifactVersion(version),
                new ArtifactDescriptor(artifactPath))
    }

    static def create(String dependencyNotation) {
        if (dependencyNotation == null) {
            throw new InvalidUserDataException("Dependency cannot be empty")
        }
        String[] dependency = dependencyNotation.split(":")
        if (dependency.size() < 3) {
            throw new InvalidUserDataException(
                    "Invalid dependency notation format. Usage: 'buildTypeId:version:artifact'"
            )
        }
        return create(dependency[0], dependency[1], dependency[2])
    }

    static def create(Map dependency) {
        if (dependency == null) {
            throw new InvalidUserDataException("Dependency cannot be empty")
        }
        return create(
                dependency["buildTypeId"],
                dependency["version"],
                dependency["artifactPath"])
    }

    def toDependencyNotation() {
        return [[group: 'org',
                 name: buildTypeId,
                 version: version.version,
                 changing: version.changing
                ],
                { ->
                    artifact {
                        name = artifactDescriptor.name
                        type = artifactDescriptor.extension
                    }
                }]
    }
}
