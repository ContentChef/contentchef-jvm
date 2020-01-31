package io.contentchef.common.request

/**
 * Data used to make requests to ContentChef's backend
 */
interface RequestData {

    /**
     * Transforms the class into a valid [Map] to be used for network requests
     */
    fun asParametersMap(): Map<String, String>
}