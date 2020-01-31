package io.contentchef.common.log

import java.util.logging.Level

/**
 * Logger interface to wrap loggers
 */
interface Logger {

    companion object {
        const val CONTENT_CHEF_LOGGER_TAG = "ContentChefLogger"
    }

    /**
     * Logs [message] and [throwable] using the provided [logLevel]
     */
    fun log(logLevel: Level, message: String? = null, throwable: Throwable? = null)

}