import com.github.jk1.tcdeps.KotlinScriptDslAdapter.teamcityServer
import com.github.jk1.tcdeps.KotlinScriptDslAdapter.pin
import com.github.jk1.tcdeps.KotlinScriptDslAdapter.tc

plugins {
    java
    id("com.github.jk1.tcdeps") version "0.17"
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
    compile(tc("bt345:1.1.50-dev-1182:kotlin-compiler1.1.50-dev-1182.zip"))
}

tasks {
    register("listDeps", Task::class) {
        doLast {
            configurations.compile.forEach {
                println(it.toString())
            }
        }
    }
}