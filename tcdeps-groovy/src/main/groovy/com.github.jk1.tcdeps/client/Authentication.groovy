package com.github.jk1.tcdeps.client

import groovy.transform.Canonical

@Canonical
class Authentication {
    String login
    String password

    boolean isRequired() {
        login && password
    }

   String asHttpHeader(){
       String encoded = "$login:$password".bytes.encodeBase64().toString()
       return "Basic $encoded"
   }
}