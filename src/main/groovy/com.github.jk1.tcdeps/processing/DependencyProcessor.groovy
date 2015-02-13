package com.github.jk1.tcdeps.processing

import com.github.jk1.tcdeps.model.DependencyDescriptor
import org.gradle.api.Project

trait DependencyProcessor {

    def Project project
    def dependencies = new ArrayList<DependencyDescriptor>()

    def configure(Project project) {
        this.project = project
    }

    def addDependency(DependencyDescriptor dependecy) {
        dependencies.add(dependecy)
    }

     def process(){}
}