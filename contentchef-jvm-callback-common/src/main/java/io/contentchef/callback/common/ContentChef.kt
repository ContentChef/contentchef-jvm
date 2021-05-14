package io.contentchef.callback.common

import io.contentchef.common.request.OnlineContentRequestData
import io.contentchef.common.request.PreviewContentRequestData
import io.contentchef.common.request.SearchOnlineRequestData
import io.contentchef.common.request.SearchPreviewRequestData
import java.util.Locale

/**
 * Exposes methods used to retrieve contents from ContentChef's backend
 */
interface ContentChef {

    /**
     * Using the [PreviewChannel] you can retrieve contents which are in in both stage and live state and even contents that are not visible in the current date
     * [previewApiKey] is the api key required for the [PreviewChannel]
     * [publishingChannel] chosen publishingChannel
     * [locale] used to retrieve localized content
     */
    fun getPreviewChannel(previewApiKey: String, publishingChannel: String, locale: Locale? = null): PreviewChannel

    /**
     * Using the [OnlineChannel] you can retrieve contents which are in live state and which are actually visible
     * [onlineApiKey] is the api key required for the [OnlineChannel]
     * [publishingChannel] chosen publishingChannel
     * [locale] used to retrieve localized content
     */
    fun getOnlineChannel(onlineApiKey: String, publishingChannel: String, locale: Locale? = null): OnlineChannel

}

typealias PreviewChannel = Channel<PreviewContentRequestData, SearchPreviewRequestData>
typealias OnlineChannel = Channel<OnlineContentRequestData, SearchOnlineRequestData>
