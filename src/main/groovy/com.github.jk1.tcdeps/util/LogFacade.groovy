package com.github.jk1.tcdeps.util


class LogFacade {

    private static final PREFIX = '[TCdeps]'

    public debug(message) {
        ResourceLocator.project.logger.debug("$PREFIX $message")
    }

    public info(message) {
        ResourceLocator.project.logger.info("$PREFIX $message")
    }

    public warn(message) {
        ResourceLocator.project.logger.warn("$PREFIX $message")
    }

    public warn(message, exception) {
        ResourceLocator.project.logger.warn("$PREFIX $message", exception)
    }
}
