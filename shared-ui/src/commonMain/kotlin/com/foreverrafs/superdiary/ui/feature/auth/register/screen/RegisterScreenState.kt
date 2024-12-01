package com.foreverrafs.superdiary.ui.feature.auth.register.screen

sealed interface RegisterScreenState {
    data object Success : RegisterScreenState
    data class Error(val error: Exception) : RegisterScreenState
    data object Idle : RegisterScreenState
    data object Processing : RegisterScreenState
}
