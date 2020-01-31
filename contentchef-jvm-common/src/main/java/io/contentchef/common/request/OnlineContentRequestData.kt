package io.contentchef.common.request

/**
 * Parameters needed to get a content from an online channel
 * [contentPublicId] the publicId of the content to be retrieved
 */
data class OnlineContentRequestData(
    val contentPublicId: String
) : RequestData {

    override fun asParametersMap(): Map<String, String> {
        return mapOf(
            QUERY_PARAM_PUBLIC_ID to contentPublicId
        )
    }

}