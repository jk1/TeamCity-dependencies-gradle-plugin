package com.github.jk1.tcdeps.model

import groovy.transform.Canonical

/**
 * https://confluence.jetbrains.com/display/TCD8/REST+API#RESTAPI-BuildLocator
 */
@Canonical
class BuildLocator {
    def String typeId
    def String number
    def String branch
}
