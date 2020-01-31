package io.contentchef.common.data

import java.util.*

/**
 * Model for a ContentChef's backend response on generic [T]
 */
sealed class ContentChefResponse<T>

/**
 * Model for a ContentChef's backend response on a generic [T] which have a single item inside [payload]
 * [publicId] is the publicId of the retrieved content
 * [definition] is the definition name of the retrieved content
 * [repository] is the repository where the retrieved content stays in
 * [onlineDate] is the online date of the content, if available
 * [offlineDate] is the offline date of the content, if available
 * [metadata] contains response metadata
 * [requestContext] contains request context details
 */
data class ContentChefItemResponse<T>(
    val publicId: String,
    val definition: String,
    val repository: String,
    val payload: T,
    val onlineDate: Date?,
    val offlineDate: Date?,
    val metadata: ContentChefResponseMetadata,
    val requestContext: ContentChefResponseRequestContext
) : ContentChefResponse<T>()

/**
 * Model for a ContentChef's backend response on a generic [T] which have multiple items inside [items]
 * [skip] is the count of the skipped items
 * [take] is the count of the taken items
 * [total] is the count of the total items
 * [contentChefResponseRequestContext] contains request context details
 */
data class ContentChefSearchResponse<T>(
    val skip: Int,
    val take: Int,
    val total: Int,
    val items: List<ContentChefItemResponse<T>>,
    val contentChefResponseRequestContext: ContentChefResponseRequestContext
) : ContentChefResponse<T>()