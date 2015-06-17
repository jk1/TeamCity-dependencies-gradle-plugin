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
        closure.setDelegate(this)
        closure.call()
    }

    def setDefaultMessage(String message){
        if (this.message == null) {
            this.message = message
        }
    }

    def assertConfigured(){
        def prefix = 'TeamCity dependencies cannot be resolved'
        if (!url){
            throw new InvalidUserDataException("$prefix: TeamCity server URL is not set")
        }
        try {
            new URL(url)
        } catch (MalformedURLException e) {
            throw new InvalidUserDataException("$prefix: $url does not look like a valid TeamCity server URL")
        }
        if (pinEnabled && (username == null || password == null)){
            throw new InvalidUserDataException("$prefix: 'username' and 'password' should be set to pin the build on TeamCity")
        }
        return true;
    }
}


