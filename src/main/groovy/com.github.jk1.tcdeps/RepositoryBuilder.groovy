package com.github.jk1.tcdeps


class RepositoryBuilder implements DependencyProcessor {

    private final TC_DOWNLOAD_PATH = 'guestAuth/repository/download'

    def process() {
        def patterns = dependencies
                .findAll { it.artifactDescriptor.hasPath() }
                .collectAll { "[module]/[revision]/${it.artifactDescriptor.path}[artifact](.[ext])" }
        project.repositories.ivy {
            url "${config.url}/$TC_DOWNLOAD_PATH"
            layout "pattern", {
                ivy '[module]/[revision]/teamcity-ivy.xml'
                artifact '[module]/[revision]/[artifact](.[ext])'
                patterns.each {
                    pattern -> artifact pattern
                }
            }
        }
    }
}
