package io.contentchef.common.exception

/**
 * Possible request error types which are then shown in a [ContentChefErrorResponse]
 */
enum class ErrorType(private val stringValue: String?) {
    GENERIC(null), VALIDATION("ValidationError");

    companion object {
        fun fromString(stringValue: String): ErrorType {
            return values().firstOrNull { it.stringValue == stringValue } ?: GENERIC
        }
    }
}