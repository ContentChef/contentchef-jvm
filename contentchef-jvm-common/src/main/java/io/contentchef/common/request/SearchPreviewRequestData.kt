package io.contentchef.common.request

import io.contentchef.common.util.ContentChefDateFormat
import io.contentchef.common.util.isNotNullNorEmpty
import java.util.*

/**
 * Parameters needed to search contents in a preview channel
 * [contentDefinitions] list of the possible contentDefinitionId a content should have to be retrieved. null to avoid applying the filter
 * [repositories] list of the possible repository a content should be in to be retrieved. null to avoid applying the filter
 * [targetDate] the date which will be used to see the preview. null to avoid applying the filter
 * [tags] list of the possible tags a content should have to be retrieved. null to avoid applying the filter
 * [publicIds] list of the possible publicId a content should have to be retrieved. null to avoid applying the filter
 * [skip] the count of content to be skipped
 * [take] the count of content to be retrieved
 * [propFilters] the filters on indexed fields to be applied to published contents. null to avoid applying the filter
 */

data class SearchPreviewRequestData @JvmOverloads constructor(
    val contentDefinitions: List<String>? = null,
    val repositories: List<String>? = null,
    val targetDate: Date? = null,
    val tags: List<String>? = null,
    val publicIds: List<String>? = null,
    val skip: Int = DEFAULT_REQUEST_SKIP_VALUE,
    val take: Int = DEFAULT_REQUEST_TAKE_VALUE,
    val propFilters: PropFilters? = null
) : RequestData {

    override fun asParametersMap(): Map<String, String> {
        val parametersMap = hashMapOf(
            QUERY_PARAM_SKIP to skip.toString(),
            QUERY_PARAM_TAKE to take.toString()
        )
        if (contentDefinitions.isNotNullNorEmpty()) {
            parametersMap[QUERY_PARAM_CONTENT_DEFINITIONS] =
                contentDefinitions!!.joinToString(separator = ",")
        }
        if (repositories.isNotNullNorEmpty()) {
            parametersMap[QUERY_PARAM_REPOSITORIES] = repositories!!.joinToString(separator = ",")
        }
        if (tags.isNotNullNorEmpty()) {
            parametersMap[QUERY_PARAM_TAGS] = tags!!.joinToString(separator = ",")
        }
        if (publicIds.isNotNullNorEmpty()) {
            parametersMap[QUERY_PARAM_PUBLIC_IDS] = publicIds!!.joinToString(separator = ",")
        }
        targetDate?.let {
            parametersMap[QUERY_PARAM_TARGET_DATE] = ContentChefDateFormat.formatDate(targetDate)
        }
        propFilters?.let {
            parametersMap[QUERY_PARAM_PROP_FILTERS] = propFilters.toJSONString()
        }
        return parametersMap.toMap()
    }
}