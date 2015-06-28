package com.github.jk1.tcdeps.model

import org.gradle.api.InvalidUserDataException


class DependencyDescriptor {

    final def String buildTypeId
    final def ArtifactDescriptor artifactDescriptor
    final def ArtifactVersion version
    final def String branch

    protected DependencyDescriptor(
            String buildTypeId, ArtifactVersion version, ArtifactDescriptor artifactDescriptor, String branch) {
        this.buildTypeId = buildTypeId
        this.version = version
        this.artifactDescriptor = artifactDescriptor
        this.branch = branch
    }

    static def create(String buildTypeId, String version, String artifactPath) {
        if (buildTypeId == null || buildTypeId.isEmpty()) {
            throw new InvalidUserDataException("buildTypeId should not be empty")
        }
        return new DependencyDescriptor(buildTypeId,
                new ArtifactVersion(version),
                new ArtifactDescriptor(artifactPath), null)
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
        def version = dependency["version"]
        def artifactVersion = new ArtifactVersion(version)
        if (!btid) {
            throw new InvalidUserDataException("buildTypeId should not be empty")
        }
        new DependencyDescriptor(btid,
                artifactVersion,
                new ArtifactDescriptor(dependency["artifactPath"]),
                dependency["branch"])
    }

    def toDependencyNotation() {
        return [[group  : 'org',
                 name   : buildTypeId,
                 version: version.version
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
        "Dependency:[buildTypeId=$buildTypeId, artifact=$artifactDescriptor, version=$version, branch=$branch]"
    }
}
