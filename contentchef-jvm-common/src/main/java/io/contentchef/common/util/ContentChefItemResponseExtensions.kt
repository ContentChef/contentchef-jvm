package io.contentchef.common.util

import io.contentchef.common.data.ContentChefItemResponse
import org.json.JSONObject

/**
 * Extension function to easily convert a [ContentChefItemResponse] from being a generic on [JSONObject] to [T]
 */
fun <T> ContentChefItemResponse<JSONObject>.toResponseWithParsedPayload(parsedPayload: T): ContentChefItemResponse<T> {
    return ContentChefItemResponse(
        publicId,
        definition,
        repository,
        parsedPayload,
        onlineDate,
        offlineDate,
        metadata,
        requestContext
    )
}