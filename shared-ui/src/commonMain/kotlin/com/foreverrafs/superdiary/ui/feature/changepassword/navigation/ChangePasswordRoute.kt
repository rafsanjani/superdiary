package com.foreverrafs.superdiary.ui.feature.changepassword.navigation

import kotlinx.serialization.Serializable

sealed interface ChangePasswordRoute {
    @Serializable
    data object ChangePasswordScreen : ChangePasswordRoute

    @Serializable
    data object PasswordChangeSuccessScreen : ChangePasswordRoute
}
