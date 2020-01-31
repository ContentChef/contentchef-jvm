package io.contentchef.common.log

import java.util.logging.Level

/**
 * Logs messages using [java.util.logging.Logger]
 */
object JavaUtilLoggingLogger : Logger {

    private val logger = java.util.logging.Logger.getLogger(Logger.CONTENT_CHEF_LOGGER_TAG)

    override fun log(logLevel: Level, message: String?, throwable: Throwable?) {
        logger.log(logLevel, message, throwable)
    }

}