package com.foreverrafs.superdiary.ui.feature.auth.register

class DeeplinkContainer {
    enum class LinkType {
        // For newly created accounts
        EmailConfirmation,

        // For magic link authentication
        Login,
    }

    private val deepLinks = mutableMapOf<LinkType, Deeplink>()

    fun add(deepLink: Deeplink) {
        require(deepLink.type !in deepLinks) {
            "Deeplink of type ${deepLink.type} already exists."
        }
        deepLinks[deepLink.type] = deepLink
    }

    fun get(type: LinkType): Deeplink? = deepLinks[type]
    fun remove(type: LinkType) = deepLinks.remove(type)
    fun clear() = deepLinks.clear()
    fun isEmpty() = deepLinks.isEmpty()
    fun isNotEmpty() = deepLinks.isNotEmpty()
}

data class Deeplink(
    val type: DeeplinkContainer.LinkType,
    val payload: String,
)
