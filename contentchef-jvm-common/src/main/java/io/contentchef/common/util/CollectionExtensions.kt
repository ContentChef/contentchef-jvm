package io.contentchef.common.util

/**
 * Returns true if [this] is not null and not empty
 */
fun <T> Collection<T>?.isNotNullNorEmpty(): Boolean {
    return this != null && this.isNotEmpty()
}