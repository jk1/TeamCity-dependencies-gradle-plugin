import com.github.jk1.tcdeps.KotlinScriptDslAdapter.teamcityServer
import com.github.jk1.tcdeps.KotlinScriptDslAdapter.pin
import com.github.jk1.tcdeps.KotlinScriptDslAdapter.tc

plugins {
    id("java")
    id("com.github.jk1.tcdeps") version "1.3"
}

repositories {
    teamcityServer {
        setUrl("https://teamcity.jetbrains.com")
        credentials {
            username = "guest"
            password = "guest"
        }
    }
}

dependencies {
    implementation(tc("bt345:1.0.0-beta-3594:kotlin-compiler-1.0.0-beta-3594.zip"))
}

tasks {
    register("listDeps", Task::class) {
        doLast {
            configurations["compileClasspath"].forEach { it ->
                println(it.toString())
            }
        }
    }
}