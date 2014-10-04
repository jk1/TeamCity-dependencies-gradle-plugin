package com.github.jk1.tcdeps

import org.gradle.api.InvalidUserDataException


class PluginConfiguration {
    def String url
    def String username
    def String password
    def boolean stopBuildOnFail
    def String message

    def boolean pinEnabled

    def pin(Closure closure){
        pinEnabled = true
        closure.call()
        if (username == null || password == null){
            throw new InvalidUserDataException("'username' and 'password' should be set to pin the build on TeamCity")
        }
    }

    def setDefaultMessage(String message){
        this.message = message
    }
}


