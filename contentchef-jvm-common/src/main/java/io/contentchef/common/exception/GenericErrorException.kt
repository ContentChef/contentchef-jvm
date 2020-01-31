package io.contentchef.common.exception

/**
 * Exception thrown in case of a generic error
 * [contentChefErrorResponse] server's response
 */
data class GenericErrorException(val contentChefErrorResponse: ContentChefErrorResponse) :
    Throwable()