package io.contentchef.common.data

import java.util.*

/**
 * Metadata of a ContentChef backend response
 * [id] publishing content's id
 * [authoringContentId] authoring content's id
 * [contentVersion] content version
 * [contentLastModifiedDate] content last modified data
 * [tags] list of tags chosen for the content
 * [publishedOn] date when the content was published
 */
data class ContentChefResponseMetadata(
    val id: Long,
    val authoringContentId: Long,
    val contentVersion: Long,
    val contentLastModifiedDate: Date,
    val tags: List<String>,
    val publishedOn: Date
)