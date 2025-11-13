package com.foreverrafs.superdiary.ui.navigation

import androidx.navigation3.runtime.NavKey
import com.foreverrafs.auth.model.UserInfo
import kotlinx.serialization.Serializable

/**
 * Root application routes where each individual route here is a nested navigation graph.
 */
sealed interface AppRoute : NavKey {

    @Serializable
    data class TopLevelGraph(
        val userInfo: UserInfo?,
    ) : AppRoute

    @Serializable
    data object CreateDiaryGraph : AppRoute

    @Serializable
    data object DiaryListGraph : AppRoute

    @Serializable
    data class AuthenticationGraph(
        val requiresNewPassword: Boolean = false,
        val showLoginScreen: Boolean = true,
        val isFromDeepLink: Boolean = false,
        val showBiometricAuth: Boolean = false,
    ) : AppRoute, NavKey

    @Serializable
    data object ProfileScreen : AppRoute
}
