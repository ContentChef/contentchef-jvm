package io.contentchef.callback.common

import io.contentchef.common.configuration.ContentChefEnvironment
import io.contentchef.common.configuration.ContentChefEnvironmentConfiguration
import io.contentchef.common.network.RequestFactory

/**
 * Exposes methods used to retrieve contents from ContentChef's backend
 */
internal class CallbackContentChef constructor(
    private val contentChefEnvironmentConfiguration: ContentChefEnvironmentConfiguration,
    private val requestFactory: RequestFactory,
    private val requestExecutor: RequestExecutor
) : ContentChef {

    override fun getPreviewChannel(
        previewApiKey: String,
        publishingChannel: String
    ): PreviewChannel {
        return AbstractChannel(
            contentChefEnvironmentConfiguration.generateWebserviceURL(
                ContentChefEnvironmentConfiguration.Companion.WebService.PREVIEW_CONTENT,
                publishingChannel
            ),
            contentChefEnvironmentConfiguration.generateWebserviceURL(
                ContentChefEnvironmentConfiguration.Companion.WebService.PREVIEW_SEARCH,
                publishingChannel
            ),
            previewApiKey,
            requestFactory,
            requestExecutor
        )
    }

    override fun getOnlineChannel(onlineApiKey: String, publishingChannel: String): OnlineChannel {
        require(contentChefEnvironmentConfiguration.contentChefEnvironment == ContentChefEnvironment.LIVE) {
            "Online channel can only be used with LIVE environment setup"
        }
        return AbstractChannel(
            contentChefEnvironmentConfiguration.generateWebserviceURL(
                ContentChefEnvironmentConfiguration.Companion.WebService.ONLINE_CONTENT,
                publishingChannel
            ),
            contentChefEnvironmentConfiguration.generateWebserviceURL(
                ContentChefEnvironmentConfiguration.Companion.WebService.ONLINE_SEARCH,
                publishingChannel
            ),
            onlineApiKey,
            requestFactory,
            requestExecutor
        )
    }

}