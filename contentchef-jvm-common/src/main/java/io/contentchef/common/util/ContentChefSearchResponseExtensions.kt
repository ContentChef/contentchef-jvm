package io.contentchef.common.util

import io.contentchef.common.data.ContentChefItemResponse
import io.contentchef.common.data.ContentChefSearchResponse
import org.json.JSONObject

/**
 * Extension function to easily convert a [ContentChefSearchResponse] from being a generic on [JSONObject] to [T]
 */
fun <T> ContentChefSearchResponse<JSONObject>.toResponseWithParsedPayload(parsedItems: List<ContentChefItemResponse<T>>): ContentChefSearchResponse<T> {
    return ContentChefSearchResponse(
        skip, take, total, parsedItems, contentChefResponseRequestContext
    )
}