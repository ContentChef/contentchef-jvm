package io.contentchef.common.log

import java.util.logging.Level

/**
 * Adapts Log levels from [java.util.logging.Level] to ERROR, WARN, INFO, DEBUG, TRACE levels as they are used by other frameworks
 */
abstract class AbstractLogger : Logger {

    override fun log(logLevel: Level, message: String?, throwable: Throwable?) {
        when (logLevel) {
            Level.SEVERE -> {
                logError(message, throwable)
            }
            Level.WARNING -> {
                logWarn(message, throwable)
            }
            Level.INFO, Level.CONFIG -> {
                logInfo(message, throwable)
            }
            Level.FINE, Level.FINER -> {
                logDebug(message, throwable)
            }
            Level.FINEST, Level.ALL -> {
                logTrace(message, throwable)
            }
        }
    }

    abstract fun logError(message: String? = null, throwable: Throwable? = null)
    abstract fun logWarn(message: String? = null, throwable: Throwable? = null)
    abstract fun logInfo(message: String? = null, throwable: Throwable? = null)
    abstract fun logDebug(message: String? = null, throwable: Throwable? = null)
    abstract fun logTrace(message: String? = null, throwable: Throwable? = null)

}