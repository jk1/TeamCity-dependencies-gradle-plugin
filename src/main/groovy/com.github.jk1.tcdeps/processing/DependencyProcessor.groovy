package com.github.jk1.tcdeps.processing

import com.github.jk1.tcdeps.model.DependencyDescriptor
import org.gradle.api.Project

trait DependencyProcessor {

    def dependencies = new ArrayList<DependencyDescriptor>()

    def addDependency(DependencyDescriptor dependecy) {
        dependencies.add(dependecy)
    }

     def process(){}
}