package com.github.jk1.tcdeps.model

/**
 * Describes changing module version used by TeamCity
 */
class ChangingModulePlaceholers {
    def staticPlaceholders = ['lastFinished'           : '',
                              'lastPinned'             : ',pinned:true',
                              'lastSuccessful'         : ',status:SUCCESS',
                              'sameChainOrLastFinished': '']

    def getAt(String key) {
        if (staticPlaceholders.containsKey(key)) {
            return staticPlaceholders[key]
        } else if (key.endsWith(".tcbuildtag")) {
            def tag = key.subSequence(0, key.lastIndexOf(".tcbuildtag"))
            return ",tags:$tag"
        } else {
            return null
        }
    }

    def containsKey(String key) {
        return staticPlaceholders.containsKey(key) || key.endsWith(".tcbuildtag")
    }
}
