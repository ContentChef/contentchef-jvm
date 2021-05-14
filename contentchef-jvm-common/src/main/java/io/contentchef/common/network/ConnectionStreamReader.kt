package io.contentchef.common.network

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection

/**
 * Reads an [InputStreamReader] an transform it into a [String]
 */
object ConnectionStreamReader {

    /**
     * Returns the content read from [httpURLConnection] as [String]
     */
    fun getContentAsString(httpURLConnection: HttpURLConnection): String {
        return when (httpURLConnection.responseCode) {
            in 200..299 -> {
                readStreamAsString(httpURLConnection.inputStream)
            }
            404 -> {
                ""
            }
            else -> {
                readStreamAsString(httpURLConnection.errorStream);
            }
        }
    }

    private fun readStreamAsString(inputStream: InputStream): String {
        return BufferedReader(InputStreamReader(inputStream)).useLines {
            it.joinToString(separator = "")
        }
    }
}