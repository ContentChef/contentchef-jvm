package io.contentchef.common.request

import io.contentchef.common.util.ContentChefDateFormat
import java.util.*

/**
 * Parameters needed to get a content from a preview channel
 * [contentPublicId] the publicId of the content to be retrieved
 * [targetDate] the date which will be used to see the preview
 */
data class PreviewContentRequestData(
    val contentPublicId: String,
    val targetDate: Date
) : RequestData {

    override fun asParametersMap(): Map<String, String> {
        return mapOf(
            QUERY_PARAM_PUBLIC_ID to contentPublicId,
            QUERY_PARAM_TARGET_DATE to ContentChefDateFormat.formatDate(targetDate)
        )
    }
}