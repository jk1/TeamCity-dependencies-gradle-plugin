package com.github.jk1.tcdeps.processing

import com.github.jk1.tcdeps.model.DependencyDescriptor

trait DependencyProcessor {

    List<DependencyDescriptor> dependencies = new ArrayList<DependencyDescriptor>()

    void addDependency(DependencyDescriptor dependency) {
        dependencies.add(dependency)
    }

    void process() {}
}