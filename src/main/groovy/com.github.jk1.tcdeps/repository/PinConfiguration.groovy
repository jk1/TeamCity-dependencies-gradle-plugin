package com.github.jk1.tcdeps.repository

class PinConfiguration {
    String url
    String username
    String password
    boolean stopBuildOnFail
    boolean pinEnabled
    String message
    String tag
    String[] excludes = []

    def setDefaultMessage(String message) {
        if (this.message == null) {
            this.message = message
        }
    }
}
