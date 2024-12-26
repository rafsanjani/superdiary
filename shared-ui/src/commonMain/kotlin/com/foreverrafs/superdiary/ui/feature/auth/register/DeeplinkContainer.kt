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
            "Deeplink of type ${deepLink.type} already exists in the container."
        }
        deepLinks[deepLink.type] = deepLink
    }

    fun getAndRemove(type: LinkType): Deeplink? {
        val value = deepLinks[type]
        deepLinks.remove(type)
        return value
    }
}

data class Deeplink(
    val type: DeeplinkContainer.LinkType,
    val payload: String,
)
