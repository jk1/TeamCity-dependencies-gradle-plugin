package com.github.jk1.tcdeps.model

import groovy.transform.Canonical

/**
 * https://confluence.jetbrains.com/display/TCD8/REST+API#RESTAPI-BuildLocator
 */
@Canonical
class BuildLocator {
    def String buildTypeId
    def Boolean pinned
    def Boolean successful
    def String branch
    def String tag

    @Override
    String toString() {
        def builder = new StringBuilder("buildType:$buildTypeId")
        if (branch) {
            builder.append(",branch:${encode(branch)}")
        }
        if (tag) {
            builder.append(",tag:${encode(tag)}")
        }
        if (pinned) {
            builder.append(",pinned:true")
        }
        if (successful) {
            builder.append(",status:SUCCESS")
        }
        return builder.toString()
    }

    private def encode(String value) {
        return URLEncoder.encode(value, "utf-8")
    }
}
