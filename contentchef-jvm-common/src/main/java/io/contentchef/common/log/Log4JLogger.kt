package io.contentchef.common.log

import org.apache.logging.log4j.LogManager

/**
 * Logs messages using Log4J [org.apache.logging.log4j.Logger]
 */
object Log4JLogger : AbstractLogger() {

    private val logger = LogManager.getLogger(Logger.CONTENT_CHEF_LOGGER_TAG)

    override fun logError(message: String?, throwable: Throwable?) {
        logger.error(message, throwable)
    }

    override fun logWarn(message: String?, throwable: Throwable?) {
        logger.warn(message, throwable)
    }

    override fun logInfo(message: String?, throwable: Throwable?) {
        logger.info(message, throwable)
    }

    override fun logDebug(message: String?, throwable: Throwable?) {
        logger.debug(message, throwable)
    }

    override fun logTrace(message: String?, throwable: Throwable?) {
        logger.trace(message, throwable)
    }

}