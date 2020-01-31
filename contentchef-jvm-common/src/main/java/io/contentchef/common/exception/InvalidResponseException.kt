package io.contentchef.common.exception

/**
 * Exception thrown in case the server response is not a valid JSON object
 * [responseContent] server's response
 */
data class InvalidResponseException(val responseContent: String) : Throwable()