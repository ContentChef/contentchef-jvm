package io.contentchef.common.util

import org.json.JSONException
import org.json.JSONObject

/**
 * Extension function to check if a [String] is a valid [JSONObject]
 * Returns true if valid, false otherwise
 */
fun String.isAValidJSONObject(): Boolean {
    return try {
        JSONObject(this)
        true
    } catch (ex: JSONException) {
        false
    }
}