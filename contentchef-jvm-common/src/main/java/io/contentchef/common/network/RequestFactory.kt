package io.contentchef.common.network

import io.contentchef.common.data.ContentChefItemResponse
import io.contentchef.common.data.ContentChefSearchResponse
import io.contentchef.common.exception.BadRequestException
import io.contentchef.common.exception.ContentNotFoundException
import io.contentchef.common.exception.GenericErrorException
import io.contentchef.common.exception.InvalidResponseException
import io.contentchef.common.log.Logger
import io.contentchef.common.network.RequestFactory.Configuration.CONNECTION_TIMEOUT_IN_MILLIS
import io.contentchef.common.network.RequestFactory.Configuration.READ_TIMEOUT_IN_MILLIS
import io.contentchef.common.util.isAValidJSONObject
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException
import java.util.logging.Level

/**
 * Generates network requests which will then be executed by a [RequestExecutor]
 */
class RequestFactory constructor(
    private val contentChefResponseMapper: ContentChefResponseMapper,
    private val connectionFactory: ConnectionFactory,
    private val connectionStreamReader: ConnectionStreamReader,
    private val logger: Logger
) {

    private object Configuration {
        const val CONNECTION_TIMEOUT_IN_MILLIS = 30000
        const val READ_TIMEOUT_IN_MILLIS = 30000
    }

    /**
     * Generates a network request using [contentChefRequestData]
     * Based on the request's endpoint [onItemSuccess] or [onSearchSuccess] will be called, not both!
     * [onError] will be called in case of an error
     */
    fun generateRequest(
        contentChefRequestData: ContentChefRequestData,
        onItemSuccess: ((contentChefResponse: ContentChefItemResponse<JSONObject>) -> Unit)? = null,
        onSearchSuccess: ((contentChefResponse: ContentChefSearchResponse<JSONObject>) -> Unit)? = null,
        onError: (throwable: Throwable) -> Unit
    ): () -> Unit {
        return {
            try {
                connectionFactory.getConnection(contentChefRequestData).use { connection ->
                    connection.connectTimeout = CONNECTION_TIMEOUT_IN_MILLIS
                    connection.readTimeout = READ_TIMEOUT_IN_MILLIS
                    connection.connect()
                    val responseContent = connectionStreamReader.getContentAsString(connection)
                    logger.log(Level.INFO, "ResponseContent $responseContent")
                    when (connection.responseCode) {
                        in 200..299 -> {
                            if (!responseContent.isAValidJSONObject()) {
                                throw InvalidResponseException(responseContent)
                            }
                            when (val contentChefResponse =
                                contentChefResponseMapper.fromJsonObjectToContentChefResponse(
                                    JSONObject(responseContent)
                                )) {
                                is ContentChefItemResponse -> {
                                    onItemSuccess!!(contentChefResponse)
                                }
                                is ContentChefSearchResponse -> {
                                    onSearchSuccess!!(contentChefResponse)
                                }
                            }
                        }
                        400 -> {
                            val contentChefErrorResponse =
                                contentChefResponseMapper.fromJsonObjectToContentChefErrorResponse(
                                    JSONObject(responseContent)
                                )
                            throw BadRequestException(contentChefErrorResponse)
                        }
                        404 -> {
                            throw ContentNotFoundException()
                        }
                        else -> {
                            val contentChefErrorResponse =
                                contentChefResponseMapper.fromJsonObjectToContentChefErrorResponse(
                                    JSONObject(responseContent)
                                )
                            throw GenericErrorException(contentChefErrorResponse)
                        }
                    }
                }
            } catch (e: UnknownHostException) {
                logger.log(Level.SEVERE, "Unknown host", e)
                onError(e)
            } catch (e: IOException) {
                logger.log(Level.SEVERE, "Can't read response", e)
                onError(e)
            } catch (e: Throwable) {
                logger.log(Level.SEVERE, "Generic error", e)
                onError(e)
            }
        }
    }

}