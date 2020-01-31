package io.contentchef.common.network

import io.contentchef.common.data.*
import io.contentchef.common.exception.ContentChefErrorResponse
import io.contentchef.common.exception.ErrorType
import io.contentchef.common.exception.InvalidJsonResponseContentException
import io.contentchef.common.util.ContentChefDateFormat
import io.contentchef.common.util.toListOf
import org.json.JSONObject
import java.util.*

/**
 * Transform ContentChef's backend response from [JSONObject] to parsed models
 */
object ContentChefResponseMapper {

    private object ResponseKeys {
        const val KEY_PUBLIC_ID = "publicId"
        const val KEY_DEFINITION = "definition"
        const val KEY_REPOSITORY = "repository"
        const val KEY_PAYLOAD = "payload"
        const val KEY_ONLINE_DATE = "onlineDate"
        const val KEY_OFFLINE_DATE = "offlineDate"
        const val KEY_METADATA = "metadata"
        const val KEY_REQUEST_CONTEXT = "requestContext"

        const val KEY_METADATA_ID = "id"
        const val KEY_METADATA_AUTHORING_CONTENT_ID = "authoringContentId"
        const val KEY_METADATA_CONTENT_VERSION = "contentVersion"
        const val KEY_METADATA_CONTENT_LAST_MODIFIED_DATE = "contentLastModifiedDate"
        const val KEY_METADATA_TAGS = "tags"
        const val KEY_METADATA_PUBLISHED_ON = "publishedOn"

        const val KEY_REQUEST_CONTEXT_PUBLISHING_CHANNEL = "publishingChannel"
        const val KEY_REQUEST_CONTEXT_TARGET_DATE = "targetDate"
        const val KEY_REQUEST_CONTEXT_CLOUD_NAME = "cloudName"
        const val KEY_REQUEST_CONTEXT_TIMESTAMP = "timestamp"

        const val KEY_SKIP = "skip"
        const val KEY_TAKE = "take"
        const val KEY_TOTAL = "total"
        const val KEY_ITEMS = "items"

        val CONTENT_CHEF_ITEM_RESPONSE_MANDATORY_KEYS = listOf(
            KEY_PUBLIC_ID,
            KEY_DEFINITION,
            KEY_REPOSITORY,
            KEY_PAYLOAD,
            KEY_ONLINE_DATE,
            KEY_OFFLINE_DATE,
            KEY_METADATA,
            KEY_REQUEST_CONTEXT
        )
        val METADATA_MANDATORY_KEYS =
            listOf(
                KEY_METADATA_ID,
                KEY_METADATA_AUTHORING_CONTENT_ID,
                KEY_METADATA_CONTENT_VERSION,
                KEY_METADATA_CONTENT_LAST_MODIFIED_DATE,
                KEY_METADATA_TAGS,
                KEY_METADATA_PUBLISHED_ON
            )
        val REQUEST_CONTEXT_KEYS = listOf(
            KEY_REQUEST_CONTEXT_PUBLISHING_CHANNEL,
            KEY_REQUEST_CONTEXT_CLOUD_NAME,
            KEY_REQUEST_CONTEXT_TIMESTAMP
        )

        val CONTENT_CHEF_SEARCH_RESPONSE_MANDATORY_KEYS =
            listOf(KEY_SKIP, KEY_TAKE, KEY_TOTAL, KEY_ITEMS, KEY_REQUEST_CONTEXT)
    }

    private object ErrorKeys {
        const val KEY_TYPE = "type"
        const val KEY_MESSAGE = "message"
        const val KEY_VALIDATIONS_ERRORS = "validationsErrors"
        const val KEY_VALIDATIONS_ERRORS_CHILDREN = "children"
        const val KEY_VALIDATIONS_ERRORS_CONSTRAINTS = "constraints"
    }

    /**
     * Transform [jsonObject] into a [ContentChefErrorResponse]
     */
    fun fromJsonObjectToContentChefErrorResponse(jsonObject: JSONObject): ContentChefErrorResponse {
        val errorType = if (jsonObject.has(ErrorKeys.KEY_TYPE)) {
            val errorTypeString = jsonObject.getString(ErrorKeys.KEY_TYPE)
            ErrorType.fromString(errorTypeString)
        } else {
            ErrorType.GENERIC
        }
        val message = jsonObject.getString(ErrorKeys.KEY_MESSAGE)
        val failedConstraints = if (jsonObject.has(ErrorKeys.KEY_VALIDATIONS_ERRORS)) {
            val validationsErrors = jsonObject.getJSONArray(ErrorKeys.KEY_VALIDATIONS_ERRORS)
            validationsErrors.toListOf<JSONObject>().map { validationsErrorJsonObject ->
                getValidationErrorsFromJsonObject(validationsErrorJsonObject)
            }
        } else {
            emptyList()
        }.flatten()
        return ContentChefErrorResponse(errorType, message, failedConstraints)
    }

    private fun getValidationErrorsFromJsonObject(jsonObject: JSONObject): List<String> {
        val childrenValidationErrors =
            if (jsonObject.has(ErrorKeys.KEY_VALIDATIONS_ERRORS_CHILDREN)) {
                val children = jsonObject.getJSONArray(ErrorKeys.KEY_VALIDATIONS_ERRORS_CHILDREN)
                if (children.length() > 0) {
                    children.toListOf<JSONObject>().map { getValidationErrorsFromJsonObject(it) }
                        .flatten().toSet()
                } else {
                    getValidationErrorConstraintsFromJsonObject(jsonObject)
                }
            } else {
                emptySet()
            }

        val validationErrors = getValidationErrorConstraintsFromJsonObject(jsonObject)

        return childrenValidationErrors.union(validationErrors).toList()
    }

    private fun getValidationErrorConstraintsFromJsonObject(jsonObject: JSONObject): Set<String> {
        return if (jsonObject.has(ErrorKeys.KEY_VALIDATIONS_ERRORS_CONSTRAINTS)) {
            val constraintsJsonObject =
                jsonObject.getJSONObject(ErrorKeys.KEY_VALIDATIONS_ERRORS_CONSTRAINTS)
            constraintsJsonObject.names()?.toListOf<String>()?.mapNotNull { key ->
                constraintsJsonObject.getString(key)
            }?.toSet() ?: emptySet()
        } else {
            emptySet()
        }
    }

    /**
     * Transform [jsonObject] into a [ContentChefResponse]
     */
    fun fromJsonObjectToContentChefResponse(jsonObject: JSONObject): ContentChefResponse<JSONObject> {
        return if (jsonObject.has(ResponseKeys.KEY_ITEMS)) {
            fromJsonObjectToContentChefSearchResponse(jsonObject)
        } else {
            fromJsonObjectToContentChefItemResponse(jsonObject)
        }
    }

    private fun fromJsonObjectToContentChefItemResponse(jsonObject: JSONObject): ContentChefItemResponse<JSONObject> {
        val contentChefItemResponseValid = validateJSONObjectFields(
            jsonObject,
            ResponseKeys.CONTENT_CHEF_ITEM_RESPONSE_MANDATORY_KEYS
        )
        if (!contentChefItemResponseValid) {
            throw InvalidJsonResponseContentException()
        }
        val metadataResponseValid = validateJSONObjectFields(
            jsonObject.getJSONObject(ResponseKeys.KEY_METADATA),
            ResponseKeys.METADATA_MANDATORY_KEYS
        )
        if (!metadataResponseValid) {
            throw InvalidJsonResponseContentException()
        }
        val requestContextValid = validateJSONObjectFields(
            jsonObject.getJSONObject(ResponseKeys.KEY_REQUEST_CONTEXT),
            ResponseKeys.REQUEST_CONTEXT_KEYS
        )
        if (!requestContextValid) {
            throw InvalidJsonResponseContentException()
        }

        val publicId = jsonObject.getString(ResponseKeys.KEY_PUBLIC_ID)
        val definition = jsonObject.getString(ResponseKeys.KEY_DEFINITION)
        val repository = jsonObject.getString(ResponseKeys.KEY_REPOSITORY)
        val payload = jsonObject.getJSONObject(ResponseKeys.KEY_PAYLOAD)
        val onlineDate = parseJsonStringToDate(jsonObject.optString(ResponseKeys.KEY_ONLINE_DATE))
        val offlineDate = parseJsonStringToDate(jsonObject.optString(ResponseKeys.KEY_OFFLINE_DATE))

        val metadata = jsonObject.getJSONObject(ResponseKeys.KEY_METADATA)
        val id = metadata.getLong(ResponseKeys.KEY_METADATA_ID)
        val authoringContentId = metadata.getLong(ResponseKeys.KEY_METADATA_AUTHORING_CONTENT_ID)
        val contentVersion = metadata.getLong(ResponseKeys.KEY_METADATA_CONTENT_VERSION)
        val contentLastModifiedDate =
            parseJsonStringToDate(metadata.getString(ResponseKeys.KEY_METADATA_CONTENT_LAST_MODIFIED_DATE))!!
        val tags = metadata.getJSONArray(ResponseKeys.KEY_METADATA_TAGS).toListOf<String>()
        val publishedOn =
            parseJsonStringToDate(metadata.getString(ResponseKeys.KEY_METADATA_PUBLISHED_ON))!!

        val contentChefResponseMetadata = ContentChefResponseMetadata(
            id, authoringContentId, contentVersion, contentLastModifiedDate, tags, publishedOn
        )

        val requestContext = jsonObject.getJSONObject(ResponseKeys.KEY_REQUEST_CONTEXT)

        val contentChefResponseRequestContext = parseRequestContext(requestContext)

        return ContentChefItemResponse(
            publicId,
            definition,
            repository,
            payload,
            onlineDate,
            offlineDate,
            contentChefResponseMetadata,
            contentChefResponseRequestContext
        )
    }

    private fun fromJsonObjectToContentChefSearchResponse(jsonObject: JSONObject): ContentChefSearchResponse<JSONObject> {
        val contentChefSearchResponseValid = validateJSONObjectFields(
            jsonObject,
            ResponseKeys.CONTENT_CHEF_SEARCH_RESPONSE_MANDATORY_KEYS
        )
        if (!contentChefSearchResponseValid) {
            throw InvalidJsonResponseContentException()
        }

        val items = jsonObject.getJSONArray(ResponseKeys.KEY_ITEMS).toListOf<JSONObject>()
        items.forEach {
            require(
                validateJSONObjectFields(
                    it,
                    ResponseKeys.CONTENT_CHEF_ITEM_RESPONSE_MANDATORY_KEYS
                )
            )
        }

        val contentChefItemList = items.map { fromJsonObjectToContentChefItemResponse(it) }

        val skip = jsonObject.getInt(ResponseKeys.KEY_SKIP)
        val take = jsonObject.getInt(ResponseKeys.KEY_TAKE)
        val total = jsonObject.getInt(ResponseKeys.KEY_TOTAL)

        val requestContext = jsonObject.getJSONObject(ResponseKeys.KEY_REQUEST_CONTEXT)

        val contentChefResponseRequestContext = parseRequestContext(requestContext)

        return ContentChefSearchResponse(
            skip,
            take,
            total,
            contentChefItemList,
            contentChefResponseRequestContext
        )
    }

    private fun parseRequestContext(requestContextJsonObject: JSONObject): ContentChefResponseRequestContext {
        val publishingChannel =
            requestContextJsonObject.getString(ResponseKeys.KEY_REQUEST_CONTEXT_PUBLISHING_CHANNEL)
        val targetDate = parseJsonDateFieldIfAvailable(
            requestContextJsonObject,
            ResponseKeys.KEY_REQUEST_CONTEXT_TARGET_DATE
        )
        val cloudName =
            requestContextJsonObject.getString(ResponseKeys.KEY_REQUEST_CONTEXT_CLOUD_NAME)
        val timestamp =
            parseJsonStringToDate(requestContextJsonObject.getString(ResponseKeys.KEY_REQUEST_CONTEXT_TIMESTAMP))!!
        return ContentChefResponseRequestContext(
            publishingChannel,
            targetDate,
            cloudName,
            timestamp
        )
    }

    @Suppress("SameParameterValue")
    private fun parseJsonDateFieldIfAvailable(jsonObject: JSONObject, responseKey: String): Date? {
        return if (jsonObject.has(responseKey)) {
            parseJsonStringToDate(jsonObject.getString(responseKey))
        } else {
            null
        }
    }

    private fun validateJSONObjectFields(
        jsonObject: JSONObject,
        mandatoryKeys: List<String>
    ): Boolean {
        return jsonObject.keys().asSequence().toList().containsAll(mandatoryKeys)
    }

    private fun parseJsonStringToDate(stringDate: String): Date? {
        return when (stringDate) {
            "", "null" -> null
            else -> ContentChefDateFormat.parseDate(stringDate)
        }
    }

}