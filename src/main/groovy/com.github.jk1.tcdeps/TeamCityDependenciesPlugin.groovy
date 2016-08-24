package com.github.jk1.tcdeps

import com.github.jk1.tcdeps.model.DependencyDescriptor
import com.github.jk1.tcdeps.processing.DependencyPinner
import com.github.jk1.tcdeps.processing.DependenciesRegexProcessor
import com.github.jk1.tcdeps.processing.ModuleVersionResolver
import com.github.jk1.tcdeps.repository.TeamCityRepositoryFactory
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.IvyArtifactRepository
import org.gradle.api.internal.ClosureBackedAction
import org.gradle.api.internal.artifacts.BaseRepositoryFactory
import org.gradle.api.internal.artifacts.dsl.DefaultRepositoryHandler
import org.gradle.util.GradleVersion

import javax.inject.Inject

import static com.github.jk1.tcdeps.util.ResourceLocator.*

class TeamCityDependenciesPlugin implements Plugin<Project> {

    private processors
    private TeamCityRepositoryFactory teamCityRepositoryFactory

    @Inject
    public TeamCityDependenciesPlugin(BaseRepositoryFactory repositoryFactory) {
        teamCityRepositoryFactory = new TeamCityRepositoryFactory(repositoryFactory)
    }


    @Override
    void apply(Project theProject) {
        assertCompatibleGradleVersion()
        processors = [new ModuleVersionResolver(), new DependencyPinner()]
        addTeamCityNotationTo theProject
        theProject.ext.tc = { Object notation ->
            setContext(theProject)
            return addDependency(DependencyDescriptor.create(notation))
        }
        theProject.afterEvaluate {
            setContext(theProject)
            processors.each { it.process() }
            new DependenciesRegexProcessor(project).process();
        }
        theProject.gradle.buildFinished { closeResourceLocator() }
    }

    private void assertCompatibleGradleVersion() {
        def current = GradleVersion.current().version.split("\\.")
        if (current[0].toInteger() < 3) {
            throw new GradleException("TeamCity dependencies plugin requires at least Gradle 3.0. ${GradleVersion.current()} detected.")
        }
    }

    private Object addDependency(DependencyDescriptor descriptor) {
        processors.each { it.addDependency(descriptor) }
        def notation = descriptor.toDependencyNotation()
        logger.debug("Dependency generated: $notation")
        return notation
    }

    private void addTeamCityNotationTo(Project project) {
        def repositories = project.repositories
        DefaultRepositoryHandler handler = repositories as DefaultRepositoryHandler;
        repositories.ext.teamcityServer = { Closure configureClosure ->
            def oldRepo = handler.findByName("TeamCity")
            if (oldRepo) {
                handler.remove(oldRepo)
            }
            def tcRepository
            if (configureClosure) {
                tcRepository = handler.addRepository(teamCityRepositoryFactory.createTeamCityRepo(), "TeamCity",
                        new ClosureBackedAction<IvyArtifactRepository>(configureClosure));
            } else {
                tcRepository = handler.addRepository(teamCityRepositoryFactory.createTeamCityRepo(), "TeamCity")
            }
            if (oldRepo) {
                project.logger.warn "Project $project already has TeamCity server configured to [$oldRepo.url], overriding with [$tcRepository.url]"
            }
            project.ext.pinConfig = tcRepository.pin
        }
    }
}
