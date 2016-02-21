package com.github.jk1.tcdeps.processing

import com.github.jk1.tcdeps.model.DependencyDescriptor

trait DependencyProcessor {

    def dependencies = new ArrayList<DependencyDescriptor>()

    def addDependency(DependencyDescriptor dependency) {
        dependencies.add(dependency)
    }

    def process() {}
}