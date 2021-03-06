package io.contentchef.sample

import io.contentchef.callback.CallbackContentChefProvider
import io.contentchef.callback.common.ContentChef
import io.contentchef.callback.common.OnlineChannel
import io.contentchef.callback.common.PreviewChannel
import io.contentchef.common.configuration.ContentChefEnvironment
import io.contentchef.common.configuration.ContentChefEnvironmentConfiguration
import io.contentchef.common.request.*
import io.contentchef.common.util.ContentChefDateFormat
import java.util.*

class Main {

    companion object {

        private lateinit var contentChef: ContentChef
        private lateinit var onlineChannel: OnlineChannel
        private lateinit var previewChannel: PreviewChannel

        //LOCALIZED CHANNELS
        private lateinit var enOnlineChannel: OnlineChannel
        private lateinit var itOnlineChannel: OnlineChannel

        @JvmStatic
        fun main(args: Array<String>) {

            contentChef = CallbackContentChefProvider.getContentChef(
                ContentChefEnvironmentConfiguration(
                    ContentChefEnvironment.LIVE, SPACE_ID
                ), true
            )

            onlineChannel = contentChef.getOnlineChannel(ONLINE_API_KEY, PUBLISHING_CHANNEL)
            previewChannel = contentChef.getPreviewChannel(PREVIEW_API_KEY, PUBLISHING_CHANNEL)

            val previewContentRequestData = PreviewContentRequestData(
                "new-header", Date()
            )

            val onlineContentRequestData = OnlineContentRequestData(
                "new-header"
            )

            val targetDate = ContentChefDateFormat.parseDate("2019-11-22T05:42:17.945-05")!!

            val searchPreviewRequestData = SearchPreviewRequestData(
                contentDefinitions = listOf("default-header"),
                targetDate = targetDate
            )

            val searchOnlineRequestData = SearchOnlineRequestData(
                contentDefinitions = listOf("default-header")
            )

            val searchPreviewWithPropFiltersRequestData = SearchPreviewRequestData(
                contentDefinitions = listOf("default-header"),
                propFilters = PropFilters.Builder()
                    .indexedFilterCondition(IndexedFilterCondition.AND)
                    .indexedFilterItem(
                        IndexedFilterItem(
                            IndexedFilterOperator.STARTS_WITH_IC,
                            "header",
                            "A"
                        )
                    )
                    .build()
            )

            previewChannel.getContent(previewContentRequestData, {
                println("onSuccess $it")
            }, {
                println("onError $it")
            })

            previewChannel.getContent(previewContentRequestData, {
                println("onSuccess $it")
            }, {
                println("onError $it")
            }, {
                SampleHeader(it.getString("header"))
            })

            onlineChannel.getContent(onlineContentRequestData, {
                println("onSuccess $it")
            }, {
                println("onError $it")
            })

            onlineChannel.getContent(onlineContentRequestData, {
                println("onSuccess $it")
            }, {
                println("onError $it")
            }, {
                SampleHeader(it.getString("header"))
            })

            previewChannel.search(searchPreviewRequestData, {
                println("onSuccess $it")
            }, {
                println("onError $it")
            })

            previewChannel.search(searchPreviewRequestData, {
                println("onSuccess $it")
            }, {
                println("onError $it")
            }, {
                SampleHeader(it.getString("header"))
            })

            onlineChannel.search(searchOnlineRequestData, {
                println("onSuccess $it")
            }, {
                println("onError $it")
            })

            onlineChannel.search(searchOnlineRequestData, {
                println("onSuccess $it")
            }, {
                println("onError $it")
            }, {
                SampleHeader(it.getString("header"))
            })

            //PROP FILTERS EXAMPLE
            previewChannel.search(searchPreviewWithPropFiltersRequestData, {
                println("onSuccess $it")
            }, {
                println("onError $it")
            })

            previewChannel.search(searchPreviewWithPropFiltersRequestData, {
                println("onSuccess $it")
            }, {
                println("onError $it")
            }, {
                SampleHeader(it.getString("header"))
            })

            //LOCALIZED CONTENT EXAMPLE
            enOnlineChannel = contentChef.getOnlineChannel(ONLINE_API_KEY, PUBLISHING_CHANNEL, locale = Locale.ENGLISH)
            itOnlineChannel = contentChef.getOnlineChannel(ONLINE_API_KEY, PUBLISHING_CHANNEL, locale = Locale.ITALY)

            val localizedHeaderOnlineContentRequestData = OnlineContentRequestData(
                "test-localized-header"
            )

            enOnlineChannel.getContent(localizedHeaderOnlineContentRequestData, {
                println("onSuccess $it")
            }, {
                println("onError $it")
            })

            itOnlineChannel.getContent(localizedHeaderOnlineContentRequestData, {
                println("onSuccess $it")
            }, {
                println("onError $it")
            })
        }
    }

}