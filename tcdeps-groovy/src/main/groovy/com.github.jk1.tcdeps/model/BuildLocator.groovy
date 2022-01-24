package com.github.jk1.tcdeps.model

import groovy.transform.Canonical

/**
 * https://confluence.jetbrains.com/display/TCD8/REST+API#RESTAPI-BuildLocator
 */
@Canonical
class BuildLocator {

    String buildTypeId
    Boolean pinned
    Boolean successful
    String branch
    String tag
    String number
    String id
    Boolean noFilter

    @Override
    String toString() {
        if (!buildTypeId){
            throw new IllegalArgumentException("Build type id is required")
        }
        def builder = new StringBuilder("buildType:${encode(buildTypeId)}")
        if (branch) {
            builder.append(",branch:${encode(branch)}")
        }
        if (tag) {
            builder.append(",tags:${encode(tag)}")
        }
        if (pinned) {
            builder.append(",pinned:true")
        }
        if (successful) {
            builder.append(",status:SUCCESS")
        }
        if (number) {
            builder.append(",number:${encode(number)}")
        }
        if (id) {
            builder.append(",id:${encode(id)}")
        }
        if (noFilter){
            builder.append(",defaultFilter:false")
        }
        return builder.toString()
    }

    private def encode(String value) {
        return URLEncoder.encode(value, "utf-8")
    }
}
