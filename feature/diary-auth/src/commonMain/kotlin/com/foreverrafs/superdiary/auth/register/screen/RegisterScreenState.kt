package com.foreverrafs.superdiary.auth.register.screen

sealed interface RegisterScreenState {
    data object Success : RegisterScreenState
    data class Error(val error: Exception) : RegisterScreenState
    data object Idle : RegisterScreenState
    data object Processing : RegisterScreenState
}
