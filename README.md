TeamCity-gradle-plugin 
======================
[![Build Status](https://travis-ci.org/jk1/TeamCity-dependencies-gradle-plugin.png?branch=master)](https://travis-ci.org/jk1/TeamCity-dependencies-gradle-plugin)

Allows the use of [JetBrains TeamCity](http://www.jetbrains.com/teamcity/) server as an external dependency repository for Gradle builds. This comes in handy when existing artifact layout ignores any established conventions, so out-of-box repository types just can't handle it.

The plugin makes use of default artifact cache, downloading each dependency only once.

### Simple example

```groovy
plugins {
    // Gradle 6
    id 'com.github.jk1.tcdeps' version '1.3.2'
}

plugins {
  // Gradle 7+
  id 'com.github.jk1.tcdeps' version '1.6.2'
}

repositories{
  teamcityServer{
    url = 'https://teamcity.jetbrains.com'
    // credentials section is optional
    credentials {
      username = "login"
      password = "password"
    }
  }
}

dependencies {
    // reference arbitrary files as artifacts
    compile tc('bt345:1.0.0-beta-3594:kotlin-compiler-1.0.0-beta-3594.zip')

    // with self-explanatory map dependency notation
    compile tc(buildTypeId: 'bt345', version: '1.0.0-beta-3594', artifactPath: 'kotlin-compiler-for-maven.jar')

    // subfolders are supported
    compile tc('bt345:1.0.0-beta-3594:KotlinJpsPlugin/kotlin-jps-plugin.jar')

    // archive traversal is available with '!' symbol
    compile tc('bt345:1.0.0-beta-3594:kotlin-compiler-1.0.0-beta-3594.zip!/kotlinc/build.txt')
    
    // as well as basic pattern-matching for artifacts
    compile tc('bt415:lastSuccessful:.*-scala.jar')
}
```
TeamCity dependency description consist of the following components: build type id, build number aka version, and artifact path. Artifact path should be relative to build artifacts root in TC build. 

### Kotlin script support

The following example demonstrates how to use the plugin in [Kotlin scripted builds](https://github.com/gradle/gradle-script-kotlin):

```
import com.github.jk1.tcdeps.KotlinScriptDslAdapter.teamcityServer
import com.github.jk1.tcdeps.KotlinScriptDslAdapter.pin
import com.github.jk1.tcdeps.KotlinScriptDslAdapter.tc

plugins {
    java
    id("com.github.jk1.tcdeps") version "1.6.2"
}


repositories {
    teamcityServer {
        setUrl("https://teamcity.jetbrains.com")
        // credentials section is optional
        credentials {
            username = "login"
            password = "password"
        }
    }
}

dependencies {
    compile(tc("bt345:1.1.50-dev-1182:kotlin-compiler1.1.50-dev-1182.zip"))
}
```

### Changing dependencies

Plugin supports TeamCity build version placeholders:

```groovy
dependencies {
    compile tc('bt351:lastFinished:plugin-verifier.jar')
    compile tc('bt131:lastPinned:javadocs/index.html')
    compile tc('bt337:lastSuccessful:odata4j.zip')
    compile tc('IntelliJIdeaCe_OpenapiJar:sameChainOrLastFinished:idea_rt.jar')
}
```

and tags with `.tcbuildtag` version suffix notation:

```groovy
dependencies {
    // Latest build marked with tag 'hub-1.0'
    compile tc('Xodus_Build:hub-1.0.tcbuildtag:console/build/libs/xodus-console.jar')
}
```

these dependencies will be resolved every build.

Changing dependencies may be also resolved against particular [feature branches](https://confluence.jetbrains.com/display/TCD8/Working+with+Feature+Branches):

```groovy
dependencies {
    compile tc(buildTypeId: 'bt390', version: 'lastSuccessful', artifactPath: 'updatePlugins.xml', branch: 'master')
}
```

Branch name should be specified exactly as it's known to TeamCity with no encoding applied.
Default branch will be used if branch value is not specified explicitly.

### Pinning the build

By default, TeamCity does not store artifacts indefinitely, deleting them after some time. To avoid dependency loss one may choose to [pin the build](https://confluence.jetbrains.com/display/TCD8/Pinned+Build) as follows:

```groovy
repositories{
  teamcityServer{
    url = 'https://teamcity.jetbrains.com'
    credentials {
      username = "login"
      password = "password"
    }
    pin {
      stopBuildOnFail = true            // not mandatory, default to 'false' 
      message = "Pinned for MyProject"  // optional pin message
      tag = "Production"                // optional build tag  
      excludes = ["MyBuildTypeId"]      // exclude build type ids from pinning/tagging  
    }
  }
}
```
"tag" property allows to assign a custom TC tag to a build your project depends on.

### Offline mode

Gradle's offline mode is fully supported for TeamCity-originated dependencies. This feature allows you to run the build when teamcity server is unreacheable or down using artifacts from local Gradle's cache:

```
gradle jar --offline
```

