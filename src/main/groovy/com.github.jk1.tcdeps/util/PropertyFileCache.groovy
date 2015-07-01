package com.github.jk1.tcdeps.util

import org.gradle.api.GradleException
import org.gradle.api.invocation.Gradle


class PropertyFileCache {

    private File file
    private Properties props = new Properties()

    public PropertyFileCache(Gradle gradle) {
        file = new File(gradle.gradleUserHomeDir, "caches/$gradle.gradleVersion/tcdeps-resolution.cache")
        try {
            if (!file.exists()){
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

    def void store(String key, String value) {
        props.setProperty(key, value)
    }

    def String load(String key) {
        props.getProperty(key)
    }

    def String flush() {
        try {
            new FileOutputStream(file).withStream {
                props.store(it, "")
            }
        } catch (IOException e) {
            throw new GradleException("TCDeps plugin failed to flush cached properties", e)
        }
    }

}
