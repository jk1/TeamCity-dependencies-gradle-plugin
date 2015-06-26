package com.github.jk1.tcdeps.util


class LogFacade {

    private static final PREFIX = '[TCdeps]'

    // initialized from TeamCityDependenciesPlugin
    static volatile logger;

    public static debug( message) {
        logger.debug("$PREFIX $message")
    }

    public static info(message) {
        logger.info("$PREFIX $message")
    }

    public static warn(message, exception) {
        logger.warn("$PREFIX $message", exception)
    }
}
