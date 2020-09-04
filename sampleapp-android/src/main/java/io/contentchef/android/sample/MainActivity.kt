package io.contentchef.android.sample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.contentchef.callback.android.CallbackContentChefProvider
import io.contentchef.callback.common.ContentChef
import io.contentchef.callback.common.OnlineChannel
import io.contentchef.callback.common.PreviewChannel
import io.contentchef.common.configuration.ContentChefEnvironment
import io.contentchef.common.configuration.ContentChefEnvironmentConfiguration
import io.contentchef.common.request.*
import io.contentchef.common.util.ContentChefDateFormat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var contentChef: ContentChef
    private lateinit var onlineChannel: OnlineChannel
    private lateinit var previewChannel: PreviewChannel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        contentChef = CallbackContentChefProvider.getContentChef(
            ContentChefEnvironmentConfiguration(ContentChefEnvironment.LIVE, SPACE_ID),
            true
        )

        onlineChannel = contentChef.getOnlineChannel(ONLINE_API_KEY, PUBLISHING_CHANNEL)
        previewChannel = contentChef.getPreviewChannel(PREVIEW_API_KEY, PUBLISHING_CHANNEL)

        fab.setOnClickListener { _ ->

            val previewContentRequestData = PreviewContentRequestData(
                contentIdEditText.text.toString(), Date()
            )

            val onlineContentRequestData = OnlineContentRequestData(
                contentIdEditText.text.toString()
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
                Log.d(TAG, "onSuccess $it")
            }, {
                Log.d(TAG, "onError", it)
            })

            previewChannel.getContent(previewContentRequestData, {
                Log.d(TAG, "onSuccess $it")
            }, {
                Log.d(TAG, "onError", it)
            }, {
                SampleHeader(it.getString("header"))
            })

            onlineChannel.getContent(onlineContentRequestData, {
                Log.d(TAG, "onSuccess $it")
            }, {
                Log.d(TAG, "onError", it)
            })

            onlineChannel.getContent(onlineContentRequestData, {
                Log.d(TAG, "onSuccess $it")
            }, {
                Log.d(TAG, "onError", it)
            }, {
                SampleHeader(it.getString("header"))
            })

            previewChannel.search(searchPreviewRequestData, {
                Log.d(TAG, "onSuccess $it")
            }, {
                Log.d(TAG, "onError", it)
            })

            previewChannel.search(searchPreviewRequestData, {
                Log.d(TAG, "onSuccess $it")
            }, {
                Log.d(TAG, "onError", it)
            }, {
                SampleHeader(it.getString("header"))
            })

            onlineChannel.search(searchOnlineRequestData, {
                Log.d(TAG, "onSuccess $it")
            }, {
                Log.d(TAG, "onError", it)
            })

            onlineChannel.search(searchOnlineRequestData, {
                Log.d(TAG, "onSuccess $it")
            }, {
                Log.d(TAG, "onError", it)
            }, {
                SampleHeader(it.getString("header"))
            })

            //PROP FILTERS EXAMPLE
            previewChannel.search(searchPreviewWithPropFiltersRequestData, {
                Log.d(TAG, "onSuccess $it")
            }, {
                Log.d(TAG, "onError $it")
            })

            previewChannel.search(searchPreviewWithPropFiltersRequestData, {
                Log.d(TAG, "onSuccess $it")
            }, {
                Log.d(TAG, "onError $it")
            }, {
                SampleHeader(it.getString("header"))
            })

        }
    }

}
