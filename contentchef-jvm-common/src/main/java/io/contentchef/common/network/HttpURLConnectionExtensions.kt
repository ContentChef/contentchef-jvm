package io.contentchef.common.network

import java.net.HttpURLConnection

/**
 * Executes the given [block] function on this resource and then closes it down correctly whether an exception
 * is thrown or not.
 *
 * [block] is a function to process this [HttpURLConnection] resource.
 */
inline fun HttpURLConnection.use(block: (HttpURLConnection) -> Unit) {
    try {
        block(this)
    } finally {
        this.disconnect()
    }
}