package io.contentchef.callback.common

import io.contentchef.common.configuration.ContentChefEnvironmentConfiguration
import io.contentchef.common.log.*
import io.contentchef.common.network.ConnectionFactory
import io.contentchef.common.network.ConnectionStreamReader
import io.contentchef.common.network.ContentChefResponseMapper
import io.contentchef.common.network.RequestFactory
import java.util.logging.Level

/**
 * Provides a [ContentChef] instance, which works using callbacks, used to communicate with the ContentChef's backend
 */
object CallbackContentChefFactory {

    /**
     * Creates a [ContentChef] instance using the provided [contentChefEnvironmentConfiguration]
     */
    fun getContentChef(
        contentChefEnvironmentConfiguration: ContentChefEnvironmentConfiguration,
        logEnabled: Boolean
    ): ContentChef {
        val logger =
            getLogger(logEnabled)
        return CallbackContentChef(
            contentChefEnvironmentConfiguration, RequestFactory(
                ContentChefResponseMapper, ConnectionFactory(logger), ConnectionStreamReader, logger
            ), ConcurrentRequestExecutor
        )
    }

    private fun getLogger(logEnabled: Boolean): Logger {
        if (!logEnabled) {
            return NoLogger
        }
        try {
            Class.forName("org.apache.logging.log4j.Logger")
            Log4JLogger.logInfo("Log4J found, using its logger.")
            return Log4JLogger
        } catch (e: ClassNotFoundException) {
            JavaUtilLoggingLogger.log(Level.INFO, "Log4J not found on classpath")
        }
        try {
            Class.forName("org.slf4j.Logger")
            SLF4JLogger.logInfo("SLF4J found, using its logger.")
            return SLF4JLogger
        } catch (e: ClassNotFoundException) {
            JavaUtilLoggingLogger.log(Level.INFO, "SLF4J not found on classpath")
        }
        JavaUtilLoggingLogger.log(Level.INFO, "Using JavaUtilLoggingLogger")
        return JavaUtilLoggingLogger
    }

}