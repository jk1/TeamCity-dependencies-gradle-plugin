package com.github.jk1.tcdeps.processing

import com.github.jk1.tcdeps.model.DependencyDescriptor

/**
 * Resolves changing module versions, e.g. lastPinned, against TeamCity feature branches.
 * It doesn't look like TeamCity's capable of customizing ivy.xml based on branch locator,
 * so we're trying to resolve exact build number beforehand to work this around
 */
class ChangingModuleVersionResolver implements DependencyProcessor {

    @Override
    def addDependency(DependencyDescriptor dependency) {
        dependency.version.resolve(project, dependency.buildTypeId)
    }


}
