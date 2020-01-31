package io.contentchef.common.network

import java.io.BufferedReader
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
        val br = if (httpURLConnection.responseCode in 200..299) {
            BufferedReader(InputStreamReader(httpURLConnection.inputStream))
        } else {
            BufferedReader(InputStreamReader(httpURLConnection.errorStream))
        }
        return br.useLines {
            it.joinToString(separator = "")
        }
    }
}