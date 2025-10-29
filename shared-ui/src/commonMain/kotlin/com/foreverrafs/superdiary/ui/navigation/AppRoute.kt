package com.foreverrafs.superdiary.ui.navigation

import androidx.navigation3.runtime.NavKey
import com.foreverrafs.auth.model.UserInfo
import kotlinx.serialization.Serializable

/**
 * Root application routes where each individual route here is a nested navigation graph.
 */
sealed interface AppRoute {
    @Serializable
    data object BiometricAuthScreen : NavKey, AppRoute

    @Serializable
    data class LoginScreen(val isFromDeeplink: Boolean = false) : NavKey, AppRoute

    @Serializable
    data object RegisterScreen : NavKey, AppRoute

    @Serializable
    data object RegistrationConfirmationScreen : NavKey, AppRoute

    @Serializable
    data class BottomNavigationNavHost(
        val userInfo: UserInfo?,
    ) : NavKey, AppRoute

    @Serializable
    data object CreateDiaryScreen : AppRoute, NavKey

    @Serializable
    data object DiaryListNavHost : AppRoute, NavKey

    @Serializable
    data class ChangePasswordNavHost(val requiresNewPassword: Boolean = false) : AppRoute, NavKey

    @Serializable
    data object ProfileScreen : AppRoute, NavKey
}
