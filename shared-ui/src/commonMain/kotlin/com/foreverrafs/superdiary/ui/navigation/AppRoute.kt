package com.foreverrafs.superdiary.ui.navigation

import androidx.navigation3.runtime.NavKey
import com.foreverrafs.auth.model.UserInfo
import kotlinx.serialization.Serializable

/**
 * Root application routes where each individual route here is a nested navigation graph.
 */
sealed interface AppRoute : NavKey {
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
    data class ChangePasswordNavHost(val requiresNewPassword: Boolean = false) : AppRoute

    @Serializable
    data object ProfileScreen : AppRoute
}
