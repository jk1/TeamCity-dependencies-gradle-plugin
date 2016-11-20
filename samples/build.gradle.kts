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

apply {
    plugin<TeamCityDependenciesPlugin>()
}

repositories {
    teamcityServer {
        url("https://teamcity.jetbrains.com")
        credentials {
            username("guest")
            password("guest")
        }
        pin {

        }
    }
}