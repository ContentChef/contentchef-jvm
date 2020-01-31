package io.contentchef.common.request

/**
 * Possible operators applied on [IndexedFilterItem]
 * _IC ones will ignore case
 */
@Suppress("unused")
enum class IndexedFilterOperator {
    EQUALS, EQUALS_IC, CONTAINS, CONTAINS_IC, IN, IN_IC, STARTS_WITH, STARTS_WITH_IC
}