package com.github.jk1.tcdeps

import org.gradle.api.InvalidUserDataException


class PluginConfiguration {
    String url
    String username
    String password
    boolean stopBuildOnFail
    boolean pinEnabled
    String message

    def pin(Closure closure){
        pinEnabled = true
        closure.call()
        if (username == null || password == null){
            throw new InvalidUserDataException("'username' and 'password' should be set to pin the build on TeamCity")
        }
    }

    def setDefaultMessage(String message){
        if (this.message == null) {
            this.message = message
        }
    }

    def assertConfigured(){
        if (url == null){
            throw new InvalidUserDataException("TeamCity dependencies cannot be resolved: TeamCity server URL is not set")
        }
    }
}


