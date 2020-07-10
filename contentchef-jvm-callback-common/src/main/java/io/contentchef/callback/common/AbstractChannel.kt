package io.contentchef.callback.common

import io.contentchef.common.data.ContentChefItemResponse
import io.contentchef.common.data.ContentChefSearchResponse
import io.contentchef.common.exception.UnableToUseProvidedMapperException
import io.contentchef.common.network.ContentChefRequestData
import io.contentchef.common.network.HttpMethod
import io.contentchef.common.network.RequestFactory
import io.contentchef.common.request.RequestData
import io.contentchef.common.util.toResponseWithParsedPayload
import org.json.JSONObject

/**
 * A channel is a collector of contents which can be used to retrieve them from ContentChef's backend
 */
internal open class AbstractChannel<C : RequestData, S : RequestData>(
    private val contentUrl: String,
    private val searchUrl: String,
    private val apiKey: String,
    private val requestFactory: RequestFactory,
    private val requestExecutor: RequestExecutor
) : Channel<C, S> {

    override fun getContent(
        contentRequestData: C,
        onSuccess: (responseContent: ContentChefItemResponse<JSONObject>) -> Unit,
        onError: (throwable: Throwable) -> Unit
    ) {
        getContent(contentRequestData, onSuccess, onError, {
            it
        })
    }

    override fun <K> getContent(
        contentRequestData: C,
        onSuccess: (responseContent: ContentChefItemResponse<K>) -> Unit,
        onError: (throwable: Throwable) -> Unit,
        mapper: (JSONObject) -> K
    ) {
        executeRequest(
            contentUrl,
            contentRequestData.asParametersMap(),
            apiKey,
            mapper,
            onItemSuccess = onSuccess,
            onError = onError
        )
    }

    override fun search(
        searchRequestData: S,
        onSuccess: (responseContent: ContentChefSearchResponse<JSONObject>) -> Unit,
        onError: (throwable: Throwable) -> Unit
    ) {
        search(searchRequestData, onSuccess, onError, { it })
    }

    override fun <K> search(
        searchRequestData: S,
        onSuccess: (responseContent: ContentChefSearchResponse<K>) -> Unit,
        onError: (throwable: Throwable) -> Unit,
        mapper: (JSONObject) -> K
    ) {
        executeRequest(
            searchUrl,
            searchRequestData.asParametersMap(),
            apiKey,
            mapper,
            onSearchSuccess = onSuccess,
            onError = onError
        )
    }

    private fun <K> executeRequest(
        url: String,
        requestParametersMap: Map<String, String>,
        apiKey: String,
        mapper: (JSONObject) -> K,
        onItemSuccess: ((responseContent: ContentChefItemResponse<K>) -> Unit)? = null,
        onSearchSuccess: ((responseContent: ContentChefSearchResponse<K>) -> Unit)? = null,
        onError: (throwable: Throwable) -> Unit
    ) {
        val contentChefRequestData = ContentChefRequestData(
            url,
            HttpMethod.GET,
            requestParametersMap,
            apiKey
        )
        val request =
            requestFactory.generateRequest(contentChefRequestData, { contentChefItemResponse ->
                try {
                    val parsedValue = mapper(contentChefItemResponse.payload)
                    onItemSuccess!!(
                        contentChefItemResponse.toResponseWithParsedPayload(
                            parsedValue
                        )
                    )
                } catch (t: Throwable) {
                    throw UnableToUseProvidedMapperException(t)
                }
            }, { contentChefSearchResponse ->
                try {
                    val parsedItemList =
                        contentChefSearchResponse.items.map { contentChefItemResponse ->
                            val parsedValue = mapper(contentChefItemResponse.payload)
                            contentChefItemResponse.toResponseWithParsedPayload(parsedValue)
                        }
                    onSearchSuccess!!(
                        contentChefSearchResponse.toResponseWithParsedPayload(
                            parsedItemList
                        )
                    )
                } catch (t: Throwable) {
                    throw UnableToUseProvidedMapperException(t)
                }
            },
                onError
            )
        requestExecutor.executeRequest(request)
    }

}