package com.github.jk1.tcdeps

import com.github.jk1.tcdeps.model.DependencyDescriptor
import com.github.jk1.tcdeps.processing.ArtifactRegexResolver
import com.github.jk1.tcdeps.processing.DependencyPinner
import com.github.jk1.tcdeps.processing.DependencyProcessor
import com.github.jk1.tcdeps.processing.ModuleVersionResolver
import com.github.jk1.tcdeps.repository.TeamCityRepositoryFactory
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.IvyArtifactRepository
import org.gradle.util.ConfigureUtil
import org.gradle.util.GradleVersion

import static com.github.jk1.tcdeps.util.ResourceLocator.*

class TeamCityDependenciesPlugin implements Plugin<Project> {

    private List<DependencyProcessor> processors
    private TeamCityRepositoryFactory teamCityRepositoryFactory = new TeamCityRepositoryFactory()

    @Override
    void apply(Project theProject) {
        assertCompatibleGradleVersion()
        setContext(theProject)
        processors = [new ModuleVersionResolver(), new DependencyPinner()]
        addTeamCityNotationTo theProject
        theProject.ext.tc = { Object notation ->
            setContext(theProject)
            return addDependency(DependencyDescriptor.create(notation))
        }
        theProject.afterEvaluate {
            setContext(theProject)
            processors.each { it.process() }
            new ArtifactRegexResolver().process()
        }
        theProject.gradle.buildFinished { closeResourceLocator() }
    }

    public IvyArtifactRepository createTeamCityRepository(Project project) {
        return teamCityRepositoryFactory.createTeamCityRepo(project)
    }

    private void assertCompatibleGradleVersion() {
        def current = GradleVersion.current().version.split("\\.")
        def major = current[0].toInteger()
        def minor = current[1].split("-")[0].toInteger()
        if (major < 5 || (major == 5 && minor < 3)) {
            throw new GradleException("TeamCity dependencies plugin requires at least Gradle 5.3. ${GradleVersion.current()} detected.")
        }
    }

    public Object addDependency(DependencyDescriptor descriptor) {
        processors.each { it.addDependency(descriptor) }
        def notation = descriptor.toDependencyNotation()
        logger.debug("Dependency generated: $notation")
        return notation
    }

    private void addTeamCityNotationTo(Project project) {
        def repositories = project.repositories
        repositories.ext.teamcityServer = { Closure configureClosure ->
            IvyArtifactRepository oldRepo = repositories.findByName("TeamCity")
            IvyArtifactRepository repo = createTeamCityRepository(project)
            if (configureClosure) {
                ConfigureUtil.configure(configureClosure, repo)
            }
            if (oldRepo) {
                project.logger.warn "Project $project already has TeamCity server [${oldRepo.getUrl()}], overriding with [${repo.getUrl()}]"
                repositories.remove(oldRepo)
            }
            repositories.add(repo)
            project.ext.pinConfig = repo.pin
            setPin(repo.pin)
        }
    }
}
