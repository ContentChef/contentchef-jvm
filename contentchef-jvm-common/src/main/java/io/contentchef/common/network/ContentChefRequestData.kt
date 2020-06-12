package io.contentchef.common.network

/**
 * Data used to make a network request to the ContentChef's backend
 * [url] the url of the request
 * [method] the [HttpMethod] of the request
 * [params] contains the parameters of the request
 * [apiKey] the api key needed for the request
 */
data class ContentChefRequestData(
    val url: String,
    val method: HttpMethod,
    val params: Map<String, String>,
    val apiKey: String
)