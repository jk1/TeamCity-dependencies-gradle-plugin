package com.github.jk1.tcdeps.model

import org.gradle.api.InvalidUserDataException


class DependencyDescriptor {

    final def buildTypeId;
    final def artifactDescriptor;
    final def version;

    protected DependencyDescriptor(
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
        if (!dependencyNotation) {
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
        def btid = dependency["buildTypeId"]
        def branch = dependency["branch"]
        def version = dependency["version"]
        def artifactVersion = branch == null ?
                new ArtifactVersion(version) :
                new ChangingModuleVersion(version, branch)
        if (!btid) {
            throw new InvalidUserDataException("buildTypeId should not be empty")
        }
        new DependencyDescriptor(btid,
                artifactVersion,
                new ArtifactDescriptor(dependency["artifactPath"]))
    }

    def toDependencyNotation() {
        return [[group   : 'org',
                 name    : buildTypeId,
                 version : version.version
                ],
                { ->
                    artifact {
                        name = artifactDescriptor.name
                        type = artifactDescriptor.extension
                    }
                }]
    }

    @Override
    String toString() {
        "Dependency:[buildTypeId=$buildTypeId, artifact=$artifactDescriptor, version=$version]"
    }
}
