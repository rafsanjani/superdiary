package com.foreverrafs.superdiary.auth.login.screen

import com.foreverrafs.auth.AuthException
import com.foreverrafs.auth.model.UserInfo

sealed interface LoginViewState {
    data class Success(val userInfo: UserInfo) : LoginViewState
    data class Error(val error: AuthException) : LoginViewState
    data object Idle : LoginViewState
    data object Processing : LoginViewState
}
