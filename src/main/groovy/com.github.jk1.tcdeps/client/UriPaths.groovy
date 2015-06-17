package com.github.jk1.tcdeps.client

import com.github.jk1.tcdeps.model.BuildLocator

def pinBuildPath = {BuildLocator locator ->

}

def getBuildNumberPath = {BuildLocator locator ->
    "/guestAuth/app/rest/builds/$locator/number"
}