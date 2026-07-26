package com.foreverrafs.superdiary.auth.register.screen

import com.foreverrafs.superdiary.auth.register.RegistrationFormErrors

sealed interface RegisterScreenState {
    data object Idle : RegisterScreenState
    data object Processing : RegisterScreenState
    data class ValidationError(val errors: RegistrationFormErrors) : RegisterScreenState
    data class Error(val error: Exception) : RegisterScreenState
    data object Success : RegisterScreenState
}
