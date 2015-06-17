package com.github.jk1.tcdeps.processing

import com.github.jk1.tcdeps.client.RequestBuilder
import com.github.jk1.tcdeps.client.RestClient
import com.github.jk1.tcdeps.model.BuildLocator
import com.github.jk1.tcdeps.model.DependencyDescriptor
import org.gradle.api.Project

/**
 * Resolves changing module versions, e.g. lastPinned, against TeamCity feature branches.
 * It doesn't look like TeamCity's capable of customizing ivy.xml based on branch locator,
 * so we're trying to resolve exact build number beforehand to work this around
 */
class ModuleVersionResolver implements DependencyProcessor {

    private def offline;
    private def placeholders = ['lastFinished'           : { return new BuildLocator() },
                                'sameChainOrLastFinished': { return new BuildLocator() },
                                'lastPinned'             : { return new BuildLocator(pinned: true) },
                                'lastSuccessful'         : { return new BuildLocator(successful: true) }]


    @Override
    def configure(Project project) {
        super.configure(project)
        offline = project.getGradle().getStartParameter().isOffline()
    }

    @Override
    def addDependency(DependencyDescriptor dependency) {
        if (dependency.getVersion().needsResolution) {
            if (offline) {
                dependency.version.version = '+' // latest from the cache
            } else {
                def BuildLocator locator = dependency.version.buildLocator
                locator.buildTypeId = dependency.buildTypeId
                locator.branch = dependency.branch

            }
        }
    }

    boolean getLocator(DependencyDescriptor dependency) {
        placeholders.containsKey(dependency.getVersion()) || dependency.getVersion().endsWith(".tcbuildtag")
    }

}
