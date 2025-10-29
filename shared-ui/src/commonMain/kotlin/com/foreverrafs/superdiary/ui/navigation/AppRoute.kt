package com.foreverrafs.superdiary.ui.navigation

import androidx.navigation3.runtime.NavKey
import com.foreverrafs.auth.model.UserInfo
import kotlinx.serialization.Serializable

/**
 * Root application routes where each individual route here is a nested navigation graph.
 */
sealed interface AppRoute : NavKey {

    @Serializable
    data class BottomNavigationNavHost(
        val userInfo: UserInfo?,
    ) : AppRoute

    @Serializable
    data object CreateDiaryScreen : AppRoute

    @Serializable
    data object DiaryListNavHost : AppRoute

    @Serializable
    data class AuthenticationNavHost(
        val requiresNewPassword: Boolean = false,
        val showLoginScreen: Boolean = true,
        val isFromDeepLink: Boolean = false,
        val showBiometricAuth: Boolean = false,
    ) : AppRoute, NavKey

    @Serializable
    data object ProfileScreen : AppRoute
}
