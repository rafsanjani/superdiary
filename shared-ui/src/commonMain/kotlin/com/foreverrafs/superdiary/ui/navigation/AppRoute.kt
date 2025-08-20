package com.foreverrafs.superdiary.ui.navigation

import com.foreverrafs.auth.model.UserInfo
import kotlinx.serialization.Serializable

/**
 * Root application routes where each individual route here is a nested navigation graph.
 */
sealed interface AppRoute {
    @Serializable
    data object BiometricAuthScreen : AppRoute

    @Serializable
    data class LoginScreen(val isFromDeeplink: Boolean = false) : AppRoute

    @Serializable
    data object RegisterScreen : AppRoute

    @Serializable
    data object RegistrationConfirmationScreen : AppRoute

    @Serializable
    data class BottomNavigationNavHost(
        val userInfo: UserInfo?,
    ) : AppRoute

    @Serializable
    data object CreateDiaryScreen : AppRoute

    @Serializable
    data object DiaryListNavHost : AppRoute

    @Serializable
    data object ChangePasswordNavHost : AppRoute

    @Serializable
    data object SendPasswordResetEmailScreen : AppRoute

    @Serializable
    data object ProfileScreen : AppRoute
}
