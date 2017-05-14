import com.github.jk1.tcdeps.KotlinScriptDslAdapter.teamcityServer
import com.github.jk1.tcdeps.KotlinScriptDslAdapter.pin
import com.github.jk1.tcdeps.KotlinScriptDslAdapter.tc
import org.gradle.script.lang.kotlin.*

// use locally built plugin
// todo: replace with composite builds?
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath(files("../build/libs/tcdeps-0.12.jar"))
    }
}

plugins.apply("com.github.jk1.tcdeps")
plugins.apply("java")


repositories {
    teamcityServer {
        setUrl("https://teamcity.jetbrains.com")
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

dependencies {
    compile(tc("bt345:1.0.0-beta-3594:kotlin-compiler-1.0.0-beta-3594.zip"))
}