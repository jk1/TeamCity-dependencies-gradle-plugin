package com.github.jk1.tcdeps

import com.github.jk1.tcdeps.util.ResourceLocator
import org.gradle.testfixtures.ProjectBuilder

trait MockProject {

    def setup(){
        ResourceLocator.setContext(ProjectBuilder.builder().build())
    }

    def cleanup(){
        ResourceLocator.closeResourceLocator()
    }
}