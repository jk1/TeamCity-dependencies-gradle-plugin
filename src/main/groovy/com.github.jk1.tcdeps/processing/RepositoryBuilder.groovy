package com.github.jk1.tcdeps.processing

import com.github.jk1.tcdeps.model.DependencyDescriptor
import org.gradle.api.artifacts.repositories.IvyArtifactRepository

/**
 * All of the artifacts for the module are then requested from the same repository
 * that was chosen in the process above
 */
class RepositoryBuilder implements DependencyProcessor {

    private final TC_DOWNLOAD_PATH = 'guestAuth/repository/download'
    private IvyArtifactRepository lastAdded

    @Override
    def addDependency(DependencyDescriptor dependecy) {
        project.repositories.remove(lastAdded)
        dependencies.add(dependecy)
        def patterns = dependencies
                .findAll { it.artifactDescriptor.hasPath() }
                .collectAll { "[module]/[revision]/${it.artifactDescriptor.path}[artifact](.[ext])" }
        lastAdded = project.repositories.ivy {
            url "${project.teamcityServer.url}/$TC_DOWNLOAD_PATH"
            layout "pattern", {
                ivy '[module]/[revision]/teamcity-ivy.xml'
                artifact '[module]/[revision]/[artifact](.[ext])'
                patterns.each {
                    pattern -> artifact pattern
                }
            }
        }
    }
}
