package io.contentchef.common.request

import org.json.JSONObject

/**
 * Contains the filters on indexed fields to be applied to published contents
 * [indexedFilterCondition] will be applied to [indexedFilterItemList]
 */
class PropFilters private constructor(
    val indexedFilterCondition: IndexedFilterCondition,
    val indexedFilterItemList: List<IndexedFilterItem>
) {

    data class Builder(
        var indexedFilterCondition: IndexedFilterCondition? = null,
        var indexedFilterItems: ArrayList<IndexedFilterItem>? = null
    ) {

        fun indexedFilterCondition(indexedFilterCondition: IndexedFilterCondition) =
            apply { this.indexedFilterCondition = indexedFilterCondition }

        fun indexedFilterItem(indexedFilterItem: IndexedFilterItem) = apply {
            if (indexedFilterItems == null) {
                indexedFilterItems = arrayListOf()
            }
            this.indexedFilterItems!!.add(indexedFilterItem)
        }

        fun build() = run {
            requireNotNull(indexedFilterCondition) { "An IndexedFilterCondition must be set!" }
            requireNotNull(indexedFilterItems) { "An list of indexedFilterItems must be set!" }
            PropFilters(indexedFilterCondition!!, indexedFilterItems!!)
        }
    }

}

fun PropFilters.toJSONString(): String {
    return JSONObject(
        mapOf(
            "condition" to indexedFilterCondition.toString(),
            "items" to indexedFilterItemList.toJSONArray()
        )
    ).toString()
}