package io.contentchef.common.network

import io.contentchef.common.log.Logger
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.logging.Level

/**
 * Creates [HttpURLConnection] used to communicate with the ContentChef's backend
 * [logger] logs HTTP stuff
 */
class ConnectionFactory(
    private val logger: Logger
) {

    companion object {
        private const val HEADER_CONTENT_CHEF_API_KEY = "X-Chef-Key"
    }

    /**
     * Generate a [HttpURLConnection] which can be used to execute a network request with [contentChefRequestData].
     * Can throw [IOException]
     */
    fun getConnection(contentChefRequestData: ContentChefRequestData): HttpURLConnection {
        try {
            var urlWithParams = contentChefRequestData.url

            if (contentChefRequestData.params.isNotEmpty()) {
                urlWithParams = urlWithParams.plus("?")
                var firstParam = true
                contentChefRequestData.params.entries.forEach { entry ->
                    if (!firstParam) {
                        urlWithParams = urlWithParams.plus("&")
                    } else {
                        firstParam = false
                    }
                    urlWithParams = urlWithParams.plus("${entry.key}=")
                    urlWithParams = try {
                        urlWithParams.plus(
                            URLEncoder.encode(
                                entry.value,
                                "UTF-8"
                            ).replace("\\+".toRegex(), "%20")
                        )
                    } catch (e: UnsupportedEncodingException) {
                        urlWithParams.plus(entry.value)
                    }

                }
            }

            logger.log(Level.INFO, "Calling url: $urlWithParams")

            val url = URL(urlWithParams)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = contentChefRequestData.method.name
            connection.setRequestProperty(
                HEADER_CONTENT_CHEF_API_KEY,
                contentChefRequestData.apiKey
            )
            return connection

        } catch (e: IOException) {
            logger.log(Level.INFO, "Can't connect to ${contentChefRequestData.url}", e)
            throw e
        }

    }
}