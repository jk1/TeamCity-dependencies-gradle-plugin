package com.github.jk1.tcdeps.repository

import org.gradle.api.InvalidUserDataException
import org.gradle.api.artifacts.repositories.IvyArtifactRepositoryMetaDataProvider
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.ResolverStrategy
import org.gradle.api.internal.artifacts.repositories.DefaultIvyArtifactRepository
import org.gradle.api.internal.artifacts.repositories.layout.AbstractRepositoryLayout
import org.gradle.api.internal.artifacts.repositories.resolver.IvyResolver
import org.gradle.api.internal.artifacts.repositories.transport.RepositoryTransport
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
class TeamCityIvyRepository extends DefaultIvyArtifactRepository {

    private final RepositoryTransportFactory transportFactory
    private final FileStore<ModuleComponentArtifactMetaData> artifactFileStore
    private final ResolverStrategy resolverStrategy
    private final LocallyAvailableResourceFinder<ModuleComponentArtifactMetaData> locallyAvailableResourceFinder

    private def pinConfig

    TeamCityIvyRepository(FileResolver fileResolver, RepositoryTransportFactory transportFactory,
                          LocallyAvailableResourceFinder<ModuleComponentArtifactMetaData> locallyAvailableResourceFinder, Instantiator instantiator,
                          ResolverStrategy resolverStrategy, FileStore<ModuleComponentArtifactMetaData> artifactFileStore) {
        super(fileResolver, transportFactory, locallyAvailableResourceFinder, instantiator, resolverStrategy, artifactFileStore);
        this.locallyAvailableResourceFinder = locallyAvailableResourceFinder
        this.resolverStrategy = resolverStrategy
        this.artifactFileStore = artifactFileStore
        this.transportFactory = transportFactory
        this.pinConfig = new PinConfiguration(this);

        layout("pattern", {
            artifact '[module]/[revision]/[artifact](.[ext])'
            ivy '[module]/[revision]/teamcity-ivy.xml'
        })
    }

    PinConfiguration getPin() {
        return pinConfig
    }

    void pin(Closure pinConfigClosure) {
        pinConfig.pinEnabled = true;
        pinConfigClosure.setDelegate(pinConfig)
        pinConfigClosure.call()
    }

    @Override
    void setUrl(Object url) {
        if (url instanceof String && !url.contains("repository/download")) {
            def substitute = url + (url.endsWith("/") ? "" : "/") + "httpAuth/repository/download"
            super.setUrl(substitute)
        } else {
            super.setUrl(url)
        }
    }

    @Override
    protected IvyResolver createRealResolver() {
        URI uri = getUrl();

        Set<String> schemes = new LinkedHashSet<String>();

        def AbstractRepositoryLayout layout = accessSuperField("layout", this)
        def AbstractRepositoryLayout additionalPatternsLayout = accessSuperField("additionalPatternsLayout", this)

        layout.addSchemes(uri, schemes);
        additionalPatternsLayout.addSchemes(uri, schemes);

        IvyResolver resolver = createResolver(schemes);

        layout.apply(uri, resolver);
        additionalPatternsLayout.apply(uri, resolver);

        return resolver;
    }

    private IvyResolver createResolver(Set<String> schemes) {
        if (schemes.isEmpty()) {
            throw new InvalidUserDataException("You must specify a base url or at least one artifact pattern for an Ivy repository.");
        }
        return createResolver(transportFactory.createTransport(schemes, getName(), getConfiguredCredentials()));
    }

    private IvyResolver createResolver(RepositoryTransport transport) {
        return new TeamCityResolver(
            getName(), transport,
            locallyAvailableResourceFinder,
            ((IvyArtifactRepositoryMetaDataProvider) accessSuperField("metaDataProvider", this)).isDynamicMode(), resolverStrategy, artifactFileStore);
    }

    private static <T> T accessSuperField(String fieldName, Object obj) {
        def field = obj.getClass().getSuperclass().getSuperclass().getDeclaredField(fieldName)
        field.setAccessible(true)
        return (T) field.get(obj);
    }
}