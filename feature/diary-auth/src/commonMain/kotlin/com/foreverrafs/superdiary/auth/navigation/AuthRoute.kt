package com.foreverrafs.superdiary.auth.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

internal sealed interface AuthRoute : NavKey {
    @Serializable
    data object ChangePasswordScreen : AuthRoute

    @Serializable
    data object PasswordChangeSuccessScreen : AuthRoute

    @Serializable
    data object SendPasswordResetEmailScreen : AuthRoute

    @Serializable
    data object BiometricAuthScreen : AuthRoute

    @Serializable
    data class LoginScreen(val isFromDeepLink: Boolean = false) : AuthRoute

    @Serializable
    data object RegisterScreen : AuthRoute

    @Serializable
    data object RegistrationConfirmationScreen : AuthRoute
}
