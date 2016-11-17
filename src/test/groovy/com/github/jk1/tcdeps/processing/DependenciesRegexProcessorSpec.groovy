package com.github.jk1.tcdeps.processing

import org.gradle.api.Project
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

import static com.github.jk1.tcdeps.util.ResourceLocator.setContext

class DependenciesRegexProcessorSpec extends Specification {

    def "Regex processor should not touch exactly matching artifacts"(){
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.github.jk1.tcdeps'
        ArtifactRegexResolver processor = new ArtifactRegexResolver()

        project.repositories.ivy {
            url = "file:///" + new File("src/test/resources/testRepo").getAbsolutePath()
        }

        project.configurations {
            testConfig
        }
        project.dependencies {
            testConfig ("org:sampleId:1234") {
                artifact {
                    name = "foobazbar"
                    type = "jar"
                }
            }
        }

        when:
        setContext(project)
        processor.process()
        def dependency = project.configurations.testConfig.dependencies.iterator().next() as ModuleDependency

        then:
        dependency.artifacts.size() == 1
        dependency.artifacts.iterator().next().name == "foobazbar"
    }


    def "Regex processor should match simple pattern"(){
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.github.jk1.tcdeps'
        ArtifactRegexResolver processor = new ArtifactRegexResolver()

        project.repositories.teamcityServer {
            url = "file:///" + new File("src/test/resources/testRepo").getAbsolutePath()
        }

        project.configurations {
            testConfig
        }

        project.dependencies {
            testConfig ("org:sampleId:1234") {
                artifact {
                    name = "foo.*bar"
                    type = "jar"
                }
            }
        }

        when:
        setContext(project)
        processor.process()
        def dependency = project.configurations.testConfig.dependencies.iterator().next() as ModuleDependency

        then:
        dependency.artifacts.size() == 2
        dependency.artifacts.find { it.name == "foobazbar" }
        dependency.artifacts.find { it.name == "foolimbar" }
    }

    def "Regex processor should handle tc(...) notation"(){
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.github.jk1.tcdeps'
        ArtifactRegexResolver processor = new ArtifactRegexResolver()

        project.repositories.teamcityServer {
            url = "file:///" + new File("src/test/resources/testRepo").getAbsolutePath()
        }

        project.configurations {
            testConfig
        }

        project.dependencies {
            testConfig project.tc("sampleId:1234:foo.*bar.jar")
        }

        when:
        setContext(project)
        processor.process()
        def dependency = project.configurations.testConfig.dependencies.iterator().next() as ModuleDependency

        then:
        dependency.artifacts.size() == 2
        dependency.artifacts.find { it.name == "foobazbar" }
        dependency.artifacts.find { it.name == "foolimbar" }
    }
}
