package com.github.jk1.tcdeps.client

import com.github.jk1.tcdeps.model.BuildLocator

class UriPaths {
    static def pinBuildPath = { BuildLocator locator ->
        "/httpAuth/app/rest/builds/$locator/pin"
    }

    static def getBuildNumberPath = { BuildLocator locator ->
        "/guestAuth/app/rest/builds/$locator/number"
    }
}

