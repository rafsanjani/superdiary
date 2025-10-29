package com.foreverrafs.superdiary.ui.feature.changepassword.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface ChangePasswordRoute : NavKey {
    @Serializable
    data object ChangePasswordScreen : ChangePasswordRoute

    @Serializable
    data object PasswordChangeSuccessScreen : ChangePasswordRoute

    @Serializable
    data object SendPasswordResetEmailScreen : ChangePasswordRoute
}
