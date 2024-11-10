package com.foreverrafs.superdiary.ui.feature.auth.login.screen

sealed interface LoginViewState {
    data object Success : LoginViewState
    data class Error(val error: Exception) : LoginViewState
    data object Idle : LoginViewState
    data object Processing : LoginViewState
}
