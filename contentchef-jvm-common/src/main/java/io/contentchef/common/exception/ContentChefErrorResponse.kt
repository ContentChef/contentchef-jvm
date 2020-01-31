package io.contentchef.common.exception

/**
 * Contains the ContentChef's backend error data
 * [errorType] the error type
 * [errorMessage] plain error message
 * [validationErrors] a list of all the happened errors
 */
data class ContentChefErrorResponse(
    val errorType: ErrorType,
    val errorMessage: String,
    val validationErrors: List<String>
)