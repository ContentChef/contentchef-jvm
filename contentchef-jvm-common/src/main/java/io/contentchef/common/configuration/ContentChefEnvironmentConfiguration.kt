package io.contentchef.common.configuration

/**
 * Used to configure a [ContentChef] instance using a [ContentChefProvider]
 * [contentChefEnvironment] chosen ContentChef environment
 */
class ContentChefEnvironmentConfiguration @JvmOverloads
/**
 * [spaceId] chosen spaceId
 * [contentChefBaseUrl] chosen endpoint url. If not provided, the default one will be used
 */
constructor(
    val contentChefEnvironment: ContentChefEnvironment,
    private val spaceId: String,
    private val contentChefBaseUrl: String = "https://api.contentchef.io"
) {

    companion object {
        private const val SPACE_ID_PLACEHOLDER = "SPACE_ID"
        private const val PUBLISHING_CHANNEL_PLACEHOLDER = "PUBLISHING_CHANNEL"
        private const val ENVIRONMENT_PLACEHOLDER = "ENVIRONMENT"

        enum class WebService(val urlTemplate: String) {
            PREVIEW_CONTENT("/space/$SPACE_ID_PLACEHOLDER/preview/$ENVIRONMENT_PLACEHOLDER/content/$PUBLISHING_CHANNEL_PLACEHOLDER"),
            PREVIEW_SEARCH("/space/$SPACE_ID_PLACEHOLDER/preview/$ENVIRONMENT_PLACEHOLDER/search/v2/$PUBLISHING_CHANNEL_PLACEHOLDER"),
            ONLINE_CONTENT("/space/$SPACE_ID_PLACEHOLDER/online/content/$PUBLISHING_CHANNEL_PLACEHOLDER"),
            ONLINE_SEARCH("/space/$SPACE_ID_PLACEHOLDER/online/search/v2/$PUBLISHING_CHANNEL_PLACEHOLDER")
        }
    }

    fun generateWebserviceURL(webService: WebService, publishingChannel: String): String {
        val finalUrl = contentChefBaseUrl.plus(webService.urlTemplate)
        return finalUrl.replace(ENVIRONMENT_PLACEHOLDER, contentChefEnvironment.urlPathValue)
            .replace(SPACE_ID_PLACEHOLDER, spaceId)
            .replace(PUBLISHING_CHANNEL_PLACEHOLDER, publishingChannel)
    }
}