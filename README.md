TeamCity-gradle-plugin
======================

Allows the use of [JetBrains TeamCity](http://www.jetbrains.com/teamcity/) server as an external dependency repository for Gradle builds. This comes in handy when existing artifact layout ignores any established conventions, so out-of-box repository types just can't handle it.

The plugin makes use of default artifact cache, downloading each dependency only once.

###Simple example

```groovy
apply plugin: 'com.github.jk1.tcdeps'

repositories{
  teamcity{
    url = 'http://teamcity.jetbrains.com'
  }
}

dependencies {
  // reference arbitrary files as artifacts
  compile tc('bt345',"0.8.1424",'kotlin-compiler-0.8.1424.zip')
  
  // subfolders are supported
  compile tc('bt345',"0.8.1424",'KotlinJpsPlugin/kotlin-jps-plugin.jar')
  
  // archive traversal is available with '!' symbol
  compile tc('bt345',"0.8.1424",'kotlin-compiler-0.8.1424.zip!/kotlinc/build.txt')
}
```
TeamCity dependency description consist of the following components: build type id, build number aka version, and artifact path. Artifact path should be relative to build artifacts root in TC build. 
