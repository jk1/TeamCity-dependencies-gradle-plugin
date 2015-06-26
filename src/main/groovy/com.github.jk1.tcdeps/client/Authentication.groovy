package com.github.jk1.tcdeps.client

import groovy.transform.Canonical
import org.apache.http.auth.Credentials
import org.apache.http.auth.UsernamePasswordCredentials

@Canonical
class Authentication {
    def String login
    def String password

    def boolean isRequired() {
        login && password
    }

    def Credentials getCredentials() {
        return new UsernamePasswordCredentials(login, password)
    }
}