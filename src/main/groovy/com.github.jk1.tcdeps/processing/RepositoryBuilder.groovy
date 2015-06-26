package com.github.jk1.tcdeps.processing

import com.github.jk1.tcdeps.util.LogFacade
import com.github.jk1.tcdeps.model.DependencyDescriptor
import org.gradle.api.artifacts.repositories.IvyArtifactRepository

/**
 * All of the artifacts for the module are then requested from the same repository
 * that was chosen in the process above
 */
class RepositoryBuilder implements DependencyProcessor {

    private final TC_DOWNLOAD_PATH = 'guestAuth/repository/download'

    private IvyArtifactRepository lastAdded
    private patterns = new ArrayList<String>()

    RepositoryBuilder() {
        // default pattern for artifacts without a path
        patterns.add('[module]/[revision]/[artifact](.[ext])')
    }

    @Override
    def addDependency(DependencyDescriptor dependency) {
        dependencies.add(dependency)
        project.repositories.remove(lastAdded)
        if (dependency.artifactDescriptor.hasPath()){
           patterns.add("[module]/[revision]/${dependency.artifactDescriptor.path}[artifact](.[ext])")
        }
        lastAdded = project.repositories.ivy {
            url "${project.teamcityServer.url}/$TC_DOWNLOAD_PATH"
            layout "pattern", {
                ivy '[module]/[revision]/teamcity-ivy.xml'
                patterns.each {
                    pattern -> artifact pattern
                }
            }
        }
    }

    @Override
    def process() {
        if (lastAdded != null) {
            LogFacade.debug('Ivy repository descriptor:')
            LogFacade.debug('ivy {')
            LogFacade.debug("  url ${lastAdded.url}")
            LogFacade.debug("  layout 'pattern', {")
            LogFacade.debug("    ivy '[module]/[revision]/teamcity-ivy.xml'")
            patterns.each {
                pattern -> LogFacade.debug("    artifact $pattern")
            }
            LogFacade.debug('  }')
            LogFacade.debug('}')
        }
    }
}
