package com.foreverrafs.superdiary.auth.register

import androidx.core.uri.Uri

class DeeplinkContainer {
    enum class LinkType(val type: String) {
        // For newly created accounts
        EmailConfirmation("confirmation"),

        // Reset password
        PasswordRecovery("recovery"),

        // When an invalid deeplink is received. This could be because of an expired OTP or an already used link
        Invalid("invalid"),

        // Magic links for one-time login
        MagicLink("magiclink"),

        // Registration
        Registration("signup"),
    }

    private var pendingLink: Deeplink? = null

    fun peek(): Deeplink? = pendingLink

    fun push(deepLink: Uri) {
        require(pendingLink == null) {
            "There is an unprocessed deeplink in the system. Unable to add more"
        }

        if (deepLink.toString().contains("error")) {
            pendingLink = Deeplink(
                type = LinkType.Invalid,
                payload = deepLink,
                isValid = false,
            )
            return
        }

        val linkType = extractTypeParameter(deepLink)

        require(linkType != null) {
            "Unable to extract link type from deeplink $deepLink"
        }

        pendingLink = Deeplink(
            type = linkType,
            payload = deepLink,
            isValid = true,
        )
    }

    private fun extractTypeParameter(deepLink: Uri): LinkType? {
        val fragment = deepLink.getFragment()
        val params = fragment?.split("&")?.associate {
            val (key, value) = it.split("=")
            key to value
        }
        val param = params?.get("type")
        return LinkType.entries.firstOrNull { it.type == param }
    }

    fun pop(): Deeplink? {
        val value = pendingLink
        pendingLink = null
        return value
    }
}

data class Deeplink(
    val type: DeeplinkContainer.LinkType,
    val payload: Uri,
    val isValid: Boolean,
)
