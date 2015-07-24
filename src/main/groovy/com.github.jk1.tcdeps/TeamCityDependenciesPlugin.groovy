package com.github.jk1.tcdeps

import com.github.jk1.tcdeps.model.DependencyDescriptor
import com.github.jk1.tcdeps.processing.DependenciesRegexProcessor
import com.github.jk1.tcdeps.processing.ModuleVersionResolver
import com.github.jk1.tcdeps.processing.DepedencyPinner
import com.github.jk1.tcdeps.processing.RepositoryBuilder

import com.github.jk1.tcdeps.repository.TeamCityIvyRepository
import com.github.jk1.tcdeps.repository.TeamCityRepositoryFactory
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.internal.ClosureBackedAction
import org.gradle.api.internal.GradleInternal
import org.gradle.api.internal.artifacts.dsl.DefaultRepositoryHandler
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.ResolverStrategy
import org.gradle.api.internal.artifacts.repositories.transport.RepositoryTransportFactory
import org.gradle.api.internal.file.FileResolver
import org.gradle.internal.component.external.model.ModuleComponentArtifactMetaData
import org.gradle.internal.reflect.Instantiator
import org.gradle.internal.resource.local.FileStore
import org.gradle.internal.resource.local.LocallyAvailableResourceFinder

import javax.inject.Inject

import static com.github.jk1.tcdeps.util.ResourceLocator.*

class TeamCityDependenciesPlugin implements Plugin<Project> {

    private processors
    private TeamCityRepositoryFactory teamCityRepositoryFactory

    @Inject
    public TeamCityDependenciesPlugin(FileResolver fileResolver,
                             RepositoryTransportFactory transportFactory,
                             LocallyAvailableResourceFinder<ModuleComponentArtifactMetaData> locallyAvailableResourceFinder,
                             Instantiator instantiator,
                             ResolverStrategy resolverStrategy,
                             GradleInternal gradleInternal) {
      def artifactFileStore = gradleInternal.getServices().get((Class<FileStore<ModuleComponentArtifactMetaData>>)(Class<?>)FileStore.class);
      teamCityRepositoryFactory = new TeamCityRepositoryFactory(fileResolver, transportFactory,
          locallyAvailableResourceFinder, instantiator, resolverStrategy, artifactFileStore);
    }


    @Override
    void apply(Project theProject) {
        processors = [new ModuleVersionResolver(), new RepositoryBuilder(), new DepedencyPinner()]
        theProject.extensions.add("teamcityServer", new PluginConfiguration())
        theProject.ext.tc = { Object notation ->
            setContext(theProject)
            theProject.teamcityServer.assertConfigured()
            return addDependency(DependencyDescriptor.create(notation))
        }
        theProject.afterEvaluate {
            setContext(theProject)
            processors.each { it.process() }
        }
        theProject.gradle.buildFinished { closeResourceLocator() }
        addTeamCityNotationTo theProject.repositories
        theProject.gradle.afterProject { Project project, failure -> new DependenciesRegexProcessor(project).process(); }
    }

    private Object addDependency(DependencyDescriptor descriptor) {
        processors.each { it.addDependency(descriptor) }
        def notation = descriptor.toDependencyNotation()
        logger.debug("Dependency generated: $notation")
        return notation
    }

    private void addTeamCityNotationTo(RepositoryHandler repositories) {
        DefaultRepositoryHandler handler = repositories as DefaultRepositoryHandler;
        repositories.ext.teamcity = { Closure configureClosure = null ->
            if (configureClosure) {
                handler.addRepository(teamCityRepositoryFactory.createTeamCityRepo(), "TeamCity",
                    new ClosureBackedAction<TeamCityIvyRepository>(configureClosure));
            } else {
                handler.addRepository(teamCityRepositoryFactory.createTeamCityRepo(), "TeamCity")
            }
        }
    }
}
