package com.github.jk1.tcdeps.repository

import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.ResolverStrategy
import org.gradle.api.internal.artifacts.repositories.transport.RepositoryTransportFactory
import org.gradle.api.internal.file.FileResolver
import org.gradle.internal.component.external.model.ModuleComponentArtifactMetaData
import org.gradle.internal.reflect.Instantiator
import org.gradle.internal.resource.local.FileStore
import org.gradle.internal.resource.local.LocallyAvailableResourceFinder

/**
 * Created by Nikita.Skvortsov
 * date: 24.07.2015.
 */
class TeamCityRepositoryFactory {
  private final FileResolver fileResolver
  private final RepositoryTransportFactory transportFactory
  private final LocallyAvailableResourceFinder<ModuleComponentArtifactMetaData> locallyAvailableResourceFinder
  private final Instantiator instantiator
  private final ResolverStrategy resolverStrategy
  private final FileStore<ModuleComponentArtifactMetaData> artifactFileStore;

  TeamCityRepositoryFactory(FileResolver fileResolver, RepositoryTransportFactory transportFactory,
                            LocallyAvailableResourceFinder<ModuleComponentArtifactMetaData> locallyAvailableResourceFinder, Instantiator instantiator,
                            ResolverStrategy resolverStrategy, FileStore<ModuleComponentArtifactMetaData> fileStore) {
    this.fileResolver = fileResolver
    this.transportFactory = transportFactory
    this.locallyAvailableResourceFinder = locallyAvailableResourceFinder
    this.instantiator = instantiator
    this.resolverStrategy = resolverStrategy
    this.artifactFileStore = fileStore
  }

  def TeamCityIvyRepository createTeamCityRepo() {
    return instantiator.newInstance(TeamCityIvyRepository.class, fileResolver, transportFactory, locallyAvailableResourceFinder, instantiator, resolverStrategy, artifactFileStore);
  }
}
