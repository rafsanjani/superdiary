package com.foreverrafs.superdiary.ui.feature.auth.login.screen

import com.foreverrafs.auth.model.UserInfo

sealed interface LoginViewState {
    data class Success(val userInfo: UserInfo) : LoginViewState
    data class Error(val error: Exception) : LoginViewState
    data object Idle : LoginViewState
    data object Processing : LoginViewState
}
