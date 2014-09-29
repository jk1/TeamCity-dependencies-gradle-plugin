package com.github.jk1.tcdeps


class DependencyDescriptor {

    final def ArtifactDescriptor artifactDescriptor;
    final def buildTypeId;
    final def version;

    public DependencyDescriptor(String buildTypeId, String version, String artifactPath) {
        this.buildTypeId = buildTypeId;
        this.version = version;
        this.artifactDescriptor = new ArtifactDescriptor(artifactPath)
    }

    public DependencyDescriptor(String dependencyNotation) {
        String[] dependency = dependencyNotation.split(":")
        this.buildTypeId = dependency[0]
        this.version = dependency[1]
        this.artifactDescriptor = new ArtifactDescriptor(dependency[2])
    }

    public DependencyDescriptor(Map dependency) {
        this.buildTypeId = dependency["buildTypeId"]
        this.version = dependency["version"]
        this.artifactDescriptor = new ArtifactDescriptor(dependency["artifactPath"])
    }

}
