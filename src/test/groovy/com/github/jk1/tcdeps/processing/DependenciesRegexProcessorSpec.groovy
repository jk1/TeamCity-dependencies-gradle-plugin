package com.github.jk1.tcdeps.processing

import org.gradle.api.Project
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * Created by Nikita.Skvortsov
 * date: 24.07.2015.
 */
class DependenciesRegexProcessorSpec extends Specification {

    def "Regex processor should not touch exactly matching artifacts"(){
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.github.jk1.tcdeps'
        DependenciesRegexProcessor processor = new DependenciesRegexProcessor(project)

        project.repositories.teamcityServer {
            url "file:///" + new File("src/test/resources/testRepo").getAbsolutePath()
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
        processor.process()
        def dependency = project.configurations.testConfig.dependencies.iterator().next() as ModuleDependency

        then:
        dependency.artifacts.size() == 1
        dependency.artifacts.iterator().next().name == "foobazbar"
    }


    def "Regex processor should match simple pattern"(){
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.github.jk1.tcdeps'
        DependenciesRegexProcessor processor = new DependenciesRegexProcessor(project)

        project.repositories.teamcityServer {
            url "file:///" + new File("src/test/resources/testRepo").getAbsolutePath()
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
        DependenciesRegexProcessor processor = new DependenciesRegexProcessor(project)

        project.repositories.teamcityServer {
            url "file:///" + new File("src/test/resources/testRepo").getAbsolutePath()
        }

        project.configurations {
            testConfig
        }

        project.dependencies {
            testConfig project.tc("sampleId:1234:foo.*bar.jar")
        }

        when:
        processor.process()
        def dependency = project.configurations.testConfig.dependencies.iterator().next() as ModuleDependency

        then:
        dependency.artifacts.size() == 2
        dependency.artifacts.find { it.name == "foobazbar" }
        dependency.artifacts.find { it.name == "foolimbar" }
    }
}
