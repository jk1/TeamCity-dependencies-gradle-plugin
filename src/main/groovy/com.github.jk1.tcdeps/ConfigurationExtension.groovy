package com.github.jk1.tcdeps


class ConfigurationExtension {
    def String url
    def String username
    def String password
    def pin(Closure closure){
        closure.call()
    }
}


