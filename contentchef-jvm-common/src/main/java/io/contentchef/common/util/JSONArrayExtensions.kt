package io.contentchef.common.util

import org.json.JSONArray

/**
 * Extension function to easily convert a [JSONArray] into a [List] of [T]
 */
fun <T> JSONArray.toListOf(): List<T> {
    val result = mutableListOf<T>()
    for (i in 0 until length()) {
        val o = this[i]
        @Suppress("UNCHECKED_CAST")
        result.add(o as T)
    }
    return result.toList()
}