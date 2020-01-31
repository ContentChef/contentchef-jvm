package io.contentchef.common.exception

/**
 * Exception thrown in case of a bad request
 * [contentChefErrorResponse] server's response
 */
data class BadRequestException(val contentChefErrorResponse: ContentChefErrorResponse) : Throwable()