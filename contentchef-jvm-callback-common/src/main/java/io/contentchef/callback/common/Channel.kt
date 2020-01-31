package io.contentchef.callback.common

import io.contentchef.common.data.ContentChefItemResponse
import io.contentchef.common.data.ContentChefSearchResponse
import io.contentchef.common.request.RequestData
import org.json.JSONObject

/**
 * A channel is a collector of contents which can be used to retrieve them from ContentChef's backend
 * [C] is the Class Type which identifies parameters used for content services
 * [S] is the Class Type which identifies parameters used for search services
 */
interface Channel<C : RequestData, S : RequestData> {

    /**
     * Retrieves a content by the parameters specified in [contentRequestData]
     * Calls [onSuccess] on a successful operation, otherwise calls [onError]
     */
    fun getContent(
        contentRequestData: C,
        onSuccess: (responseContent: ContentChefItemResponse<JSONObject>) -> Unit,
        onError: (throwable: Throwable) -> Unit
    )

    /**
     * Retrieves a content by the parameters specified in [contentRequestData]
     * Calls [onSuccess] on a successful operation, otherwise calls [onError]
     * The [mapper] is used to transform the [JSONObject] into [K]. If an error happens during the transformation [onError] will be called
     */
    fun <K> getContent(
        contentRequestData: C,
        onSuccess: (responseContent: ContentChefItemResponse<K>) -> Unit,
        onError: (throwable: Throwable) -> Unit,
        mapper: (JSONObject) -> K
    )

    /**
     * Retrieves a list of content based on the chosen search criteria as specified in [searchRequestData]
     * Calls [onSuccess] on a successful operation, otherwise calls [onError]
     */
    fun search(
        searchRequestData: S,
        onSuccess: (responseContent: ContentChefSearchResponse<JSONObject>) -> Unit,
        onError: (throwable: Throwable) -> Unit
    )

    /**
     * Retrieves a list of content based on the chosen search criteria as specified in [searchRequestData]
     * Calls [onSuccess] on a successful operation, otherwise calls [onError]
     * The [mapper] is used to transform the [JSONObject] into [K]. If an error happens during the transformation [onError] will be called
     */
    fun <K> search(
        searchRequestData: S,
        onSuccess: (responseContent: ContentChefSearchResponse<K>) -> Unit,
        onError: (throwable: Throwable) -> Unit,
        mapper: (JSONObject) -> K
    )
}