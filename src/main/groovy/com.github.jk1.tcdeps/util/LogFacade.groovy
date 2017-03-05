package com.github.jk1.tcdeps.util


class LogFacade {

    private static final PREFIX = '[TCdeps]'

    def debug(message) {
        ResourceLocator.project.logger.debug("$PREFIX $message")
    }

    def info(message) {
        ResourceLocator.project.logger.info("$PREFIX $message")
    }

    def warn(message) {
        ResourceLocator.project.logger.warn("$PREFIX $message")
    }

    def warn(message, exception) {
        ResourceLocator.project.logger.warn("$PREFIX $message", exception)
    }
}
