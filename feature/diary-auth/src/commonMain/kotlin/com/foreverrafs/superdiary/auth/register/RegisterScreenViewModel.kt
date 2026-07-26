package com.foreverrafs.superdiary.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.superdiary.auth.register.screen.RegisterScreenState
import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterScreenViewModel(
    private val authApi: AuthApi,
    private val coroutineDispatchers: AppCoroutineDispatchers,
    private val formValidator: RegistrationFormValidator,
) : ViewModel() {
    private val _viewState: MutableStateFlow<RegisterScreenState> =
        MutableStateFlow(RegisterScreenState.Idle)

    val viewState = _viewState.asStateFlow()

    /** Called when the user modifies any input field — clears stale validation errors. */
    fun onFieldChanged() {
        if (_viewState.value is RegisterScreenState.ValidationError) {
            _viewState.update { RegisterScreenState.Idle }
        }
    }

    fun onRegisterClick(
        name: String,
        email: String,
        password: String,
        verifyPassword: String,
    ) = viewModelScope.launch(coroutineDispatchers.main) {
        // 1. Validate the form
        val validationResult = formValidator.validate(
            RegistrationFormData(
                name = name,
                email = email,
                password = password,
                verifyPassword = verifyPassword,
            ),
        )

        when (validationResult) {
            is RegistrationFormValidationResult.Invalid -> {
                _viewState.update {
                    RegisterScreenState.ValidationError(errors = validationResult.errors)
                }
                return@launch
            }

            is RegistrationFormValidationResult.Valid -> { /* proceed */ }
        }

        // 2. Attempt registration
        _viewState.update {
            RegisterScreenState.Processing
        }

        when (
            val result = authApi.register(
                name = name,
                email = email,
                password = password,
            )
        ) {
            is AuthApi.RegistrationStatus.Error -> _viewState.update {
                RegisterScreenState.Error(
                    error = result.exception,
                )
            }

            is AuthApi.RegistrationStatus.Success -> _viewState.update {
                RegisterScreenState.Success
            }
        }
    }
}
