package io.contentchef.common.data

import java.util.*

/**
 * RequestContext of a ContentChef backend response
 * [publishingChannel] channel used to retrieve the content
 * [targetDate] chosen preview date. It is only available for preview endpoints. See [ContentChefEnvironmentConfiguration]
 * [cloudName] infrastructure cloud name
 * [timestamp] request/response timestamp
 */
data class ContentChefResponseRequestContext(
    val publishingChannel: String,
    val targetDate: Date?,
    val cloudName: String,
    val timestamp: Date
)