package io.contentchef.common.request

import org.json.JSONArray
import org.json.JSONObject

/**
 * Represents a filter [operator] applied on an indexed [field] with a [value]
 */
data class IndexedFilterItem(
    val operator: IndexedFilterOperator,
    val field: String,
    val value: String
)

fun List<IndexedFilterItem>.toJSONArray(): JSONArray {
    return JSONArray(map { it.toJSONObject() })
}

fun IndexedFilterItem.toJSONObject(): JSONObject {
    return JSONObject(
        mapOf(
            IndexedFilterItem::operator.name to operator.toString(),
            IndexedFilterItem::field.name to field,
            IndexedFilterItem::value.name to value
        )
    )
}