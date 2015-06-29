package com.github.jk1.tcdeps.client

import groovy.transform.Canonical

@Canonical
class Authentication {
    def String login
    def String password

    def boolean isRequired() {
        login && password
    }

   def String asHttpHeader(){
       String encoded = "$login:$password".bytes.encodeBase64().toString()
       return "Basic $encoded"
   }
}