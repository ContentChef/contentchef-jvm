package io.contentchef.callback

import io.contentchef.callback.common.CallbackContentChefFactory
import io.contentchef.callback.common.ContentChef
import io.contentchef.common.configuration.ContentChefEnvironmentConfiguration

/**
 * Provides a [ContentChef] instance, which works using callbacks, used to communicate with the ContentChef's backend
 */
object CallbackContentChefProvider {

    /**
     * Creates a [ContentChef] instance using the provided [contentChefEnvironmentConfiguration]
     */
    @JvmStatic
    fun getContentChef(
        contentChefEnvironmentConfiguration: ContentChefEnvironmentConfiguration,
        logEnabled: Boolean
    ): ContentChef {
        return CallbackContentChefFactory.getContentChef(
            contentChefEnvironmentConfiguration,
            logEnabled
        )
    }

}