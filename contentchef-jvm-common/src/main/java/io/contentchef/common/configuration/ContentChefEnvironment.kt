@file:Suppress("unused")

package io.contentchef.common.configuration

enum class ContentChefEnvironment(val urlPathValue: String) {
    STAGING("staging"), LIVE("live")
}