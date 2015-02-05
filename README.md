TeamCity-gradle-plugin 
======================
[![Build Status](https://travis-ci.org/jk1/TeamCity-dependencies-gradle-plugin.png?branch=master)](https://travis-ci.org/jk1/TeamCity-dependencies-gradle-plugin)

[ ![Download](https://api.bintray.com/packages/eugenbox/maven/teamcity-dependencies-gradle-plugin/images/download.svg) ](https://bintray.com/eugenbox/maven/teamcity-dependencies-gradle-plugin/_latestVersion)

Allows the use of [JetBrains TeamCity](http://www.jetbrains.com/teamcity/) server as an external dependency repository for Gradle builds. This comes in handy when existing artifact layout ignores any established conventions, so out-of-box repository types just can't handle it.

The plugin makes use of default artifact cache, downloading each dependency only once.

###Simple example

```groovy
// for Gradle 2.1+
plugins {
  id 'com.github.jk1.tcdeps' version '0.3'
}

// for Gradle 2.0 and below
apply plugin: 'com.github.jk1.tcdeps'

repositories{
  teamcityServer{
    url = 'http://teamcity.jetbrains.com'
  }
}

dependencies {
    // reference arbitrary files as artifacts
    compile tc('bt345:0.10.195:kotlin-compiler-0.10.195.zip')

    // with self-explanatory map dependency notation
    compile tc(buildTypeId: 'bt345', version: '0.10.195', artifactPath: 'kotlin-compiler-for-maven.jar')

    // subfolders are supported
    compile tc('bt345:0.10.195:KotlinJpsPlugin/kotlin-jps-plugin.jar')

    // archive traversal is available with '!' symbol
    compile tc('bt345:0.10.195:kotlin-compiler-0.10.195.zip!/kotlinc/build.txt')

    // use TeamCity version aliases to declare snapshot-like dependencies
    compile tc('bt351:lastFinished:plugin-verifier.jar')
    compile tc('bt345:lastPinned:internal/kotlin-test-data.zip')
    compile tc('bt337:lastSuccessful:odata4j.zip')
}
```
TeamCity dependency description consist of the following components: build type id, build number aka version, and artifact path. Artifact path should be relative to build artifacts root in TC build. 

###Pinning the build

By default, TeamCity does not store artifacts indefinitely, deleting them after some time. To avoid dependency loss one may choose to [pin the build](https://confluence.jetbrains.com/display/TCD8/Pinned+Build) as follows:

```groovy
repositories{
  teamcityServer{
    url = 'http://teamcity.jetbrains.com'
    pin {
      // pinning usually requires authnetication
      username = "name"
      password = "secret"
      stopBuildOnFail = true  // not mandatory, default to 'false'
      message = "Pinned for MyCoolProject"  // not mandatory
    }
  }
}
```
