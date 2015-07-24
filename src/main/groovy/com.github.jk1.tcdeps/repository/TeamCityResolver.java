package com.github.jk1.tcdeps.repository;

import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.ResolverStrategy;
import org.gradle.api.internal.artifacts.repositories.resolver.IvyResolver;
import org.gradle.api.internal.artifacts.repositories.transport.RepositoryTransport;
import org.gradle.internal.component.external.model.ModuleComponentArtifactMetaData;
import org.gradle.internal.component.model.DefaultIvyArtifactName;
import org.gradle.internal.component.model.IvyArtifactName;
import org.gradle.internal.resource.local.FileStore;
import org.gradle.internal.resource.local.LocallyAvailableResourceFinder;

/**
 * Created by Nikita.Skvortsov
 * date: 14.07.2015.
 */
public class TeamCityResolver extends IvyResolver {

  public TeamCityResolver(String name, RepositoryTransport transport, LocallyAvailableResourceFinder<ModuleComponentArtifactMetaData> locallyAvailableResourceFinder, boolean dynamicResolve, ResolverStrategy resolverStrategy, FileStore<ModuleComponentArtifactMetaData> artifactFileStore) {
    super(name, transport, locallyAvailableResourceFinder, dynamicResolve, resolverStrategy, artifactFileStore);
  }

  @Override
  protected IvyArtifactName getMetaDataArtifactName(String moduleName) {
    return new DefaultIvyArtifactName("teamcity-ivy", "ivy", "xml");
  }
}
