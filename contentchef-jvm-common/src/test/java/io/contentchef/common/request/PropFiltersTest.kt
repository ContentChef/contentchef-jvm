package io.contentchef.common.request

import org.junit.Assert.assertEquals
import org.junit.Test

class PropFiltersTest {

    @Test
    fun whenPropFiltersAreTransformedToJSONThenCheckOutputIsAValidJSON() {
        val propFilters = PropFilters.Builder()
            .indexedFilterCondition(IndexedFilterCondition.AND)
            .indexedFilterItem(
                IndexedFilterItem(
                    IndexedFilterOperator.STARTS_WITH,
                    "header",
                    "A"
                )
            )
            .build()

        assertEquals(
            "{\"items\":[{\"field\":\"header\",\"value\":\"A\",\"operator\":\"STARTS_WITH\"}],\"condition\":\"AND\"}",
            propFilters.toJSONString()
        )
    }


}