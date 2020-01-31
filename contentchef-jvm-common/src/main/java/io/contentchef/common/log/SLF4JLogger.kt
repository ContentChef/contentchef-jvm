package io.contentchef.common.log

import io.contentchef.common.log.Logger.Companion.CONTENT_CHEF_LOGGER_TAG
import org.slf4j.LoggerFactory


/**
 * Logs messages using SLF4J [org.slf4j.Logger]
 */
object SLF4JLogger : AbstractLogger() {

    private val logger = LoggerFactory.getLogger(CONTENT_CHEF_LOGGER_TAG)

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