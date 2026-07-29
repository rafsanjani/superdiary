package com.foreverrafs.superdiary.auth.register

import androidx.core.uri.Uri
import kotlinx.serialization.Serializable

data class AuthDeepLink(
    val type: LinkType,
    val payload: Uri,
) {
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
}

@Serializable
data class AuthDeepLinkMatch(val type: String)

@Serializable
data object InvalidAuthDeepLinkMatch
