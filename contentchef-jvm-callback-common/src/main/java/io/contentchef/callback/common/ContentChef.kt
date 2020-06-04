package io.contentchef.callback.common

import io.contentchef.common.request.OnlineContentRequestData
import io.contentchef.common.request.PreviewContentRequestData
import io.contentchef.common.request.SearchOnlineRequestData
import io.contentchef.common.request.SearchPreviewRequestData

/**
 * Exposes methods used to retrieve contents from ContentChef's backend
 */
interface ContentChef {

    /**
     * Using the [PreviewChannel] you can retrieve contents which are in in both stage and live state and even contents that are not visible in the current date
     * [publishingChannel] chosen publishingChannel
     * [previewApiKey] is the api key required for the [PreviewChannel]
     */
    fun getPreviewChannel(previewApiKey: String, publishingChannel: String): PreviewChannel

    /**
     * Using the [OnlineChannel] you can retrieve contents which are in live state and which are actually visible
     * [publishingChannel] chosen publishingChannel
     * [onlineApiKey] is the api key required for the [OnlineChannel]
     */
    fun getOnlineChannel(onlineApiKey: String, publishingChannel: String): OnlineChannel

}

typealias PreviewChannel = Channel<PreviewContentRequestData, SearchPreviewRequestData>
typealias OnlineChannel = Channel<OnlineContentRequestData, SearchOnlineRequestData>
