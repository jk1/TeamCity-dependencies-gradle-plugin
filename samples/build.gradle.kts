import com.github.jk1.tcdeps.TeamCityDependenciesPlugin
import com.github.jk1.tcdeps.KotlinScriptDslAdapter.teamcityServer
import com.github.jk1.tcdeps.KotlinScriptDslAdapter.pin

// use locally built plugin
// todo: replace with composite builds?
buildscript {
    repositories { mavenCentral() }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-runtime:1.0.5")
        classpath(files("../build/libs/tcdeps-0.12.jar"))
    }
}

//you could use plugins.apply("com.github.jk1.tcdeps")
apply {
    plugin<TeamCityDependenciesPlugin>()
}

repositories {
    teamcityServer {
        url("https://teamcity.jetbrains.com")
        credentials {
            username = "guest"
            password = "guest"
        }
        pin {
            // pinning usually requires authentication
            username = "user"
            password = "secret"
            stopBuildOnFail = true            // not mandatory, default to 'false'
            message = "Pinned for MyProject"  // optional pin message
            tag = "test"                // optional build tag
        }
    }
}