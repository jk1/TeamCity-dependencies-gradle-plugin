package com.github.jk1.tcdeps.util

import org.gradle.api.GradleException
import org.gradle.wrapper.GradleUserHomeLookup

class PropertyFileCache {

    private File file
    private Properties props = new Properties()

    PropertyFileCache() {
        file = new File(GradleUserHomeLookup.gradleUserHome(), "caches/tcdeps-resolution.cache")
        try {
            if (!file.exists()) {
                file.parentFile.mkdirs()
                file.createNewFile()
            }
            new FileInputStream(file).withStream {
                props.load(it)
            }
        } catch (IOException e) {
            throw new GradleException("TCDeps plugin failed to read cache file", e)
        }
    }

    void store(String key, String value) {
        props.setProperty(key, value)
    }

    String load(String key) {
        props.getProperty(key)
    }

    String flush() {
        try {
            new FileOutputStream(file).withStream {
                props.store(it, 'TeamCity dependencies Gradle plugin cache file. https://github.com/jk1/TeamCity-dependencies-gradle-plugin')
            }
        } catch (IOException e) {
            throw new GradleException("TCDeps plugin failed to flush cached properties", e)
        }
    }

}
