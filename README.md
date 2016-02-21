TeamCity-gradle-plugin 
======================
[![Build Status](https://travis-ci.org/jk1/TeamCity-dependencies-gradle-plugin.png?branch=master)](https://travis-ci.org/jk1/TeamCity-dependencies-gradle-plugin)

Allows the use of [JetBrains TeamCity](http://www.jetbrains.com/teamcity/) server as an external dependency repository for Gradle builds. This comes in handy when existing artifact layout ignores any established conventions, so out-of-box repository types just can't handle it.

The plugin makes use of default artifact cache, downloading each dependency only once.

###Simple example

```groovy
// for Gradle 2.7+
plugins {
  id 'com.github.jk1.tcdeps' version '0.9'
}

// for Gradle 2.1-2.6
plugins {
  id 'com.github.jk1.tcdeps' version '0.7.7'
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
}
```
TeamCity dependency description consist of the following components: build type id, build number aka version, and artifact path. Artifact path should be relative to build artifacts root in TC build. 

###Changing dependencies

Plugin supports TeamCity's build version placeholders:

```groovy
dependencies {
    compile tc('bt351:lastFinished:plugin-verifier.jar')
    compile tc('bt131:lastPinned:javadocs/index.html')
    compile tc('bt337:lastSuccessful:odata4j.zip')
    compile tc('bt132:sameChainOrLastFinished:index.html')
}
```
these dependencies will be resolved every build.

Changing dependencies may be also resolved against particular [feature branches](https://confluence.jetbrains.com/display/TCD8/Working+with+Feature+Branches):

```groovy
dependencies {
    compile tc(buildTypeId: 'bt345', version: 'lastSuccessful', artifactPath: 'KotlinJpsPlugin/kotlin-jps-plugin.jar', branch: 'master')
}
```

Branch name should be specified exactly as it's known to TeamCity with no encoding applied.
Default branch will be used if branch value is not specified explicitly.

###Pinning the build

By default, TeamCity does not store artifacts indefinitely, deleting them after some time. To avoid dependency loss one may choose to [pin the build](https://confluence.jetbrains.com/display/TCD8/Pinned+Build) as follows:

```groovy
repositories{
  teamcityServer{
    url = 'http://teamcity.jetbrains.com'
    pin {
      // pinning usually requires authentication
      username = "name"
      password = "secret"
      stopBuildOnFail = true            // not mandatory, default to 'false' 
      message = "Pinned for MyProject"  // optional pin message
      tag = "Production"                // optional build tag  
      excludes = ["MyBuildTypeId"]      // exclude build type ids from pinning/tagging  
    }
  }
}
```
"tag" property allows to assign a custom TC tag to a build your project depends on.

###Offline mode

Gradle's offline mode is fully supported for TeamCity-originated dependencies. This feature allows you to run the build when teamcity server is unreacheable or down using artifacts from local Gradle's cache:

```
gradle jar --offline
```

