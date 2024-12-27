package com.foreverrafs.superdiary.ui.feature.auth.reset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.auth.AuthApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PasswordResetViewState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isEmailValid: Boolean = false,
    val inputErrorMessage: String? = null,
    val isEmailSent: Boolean? = null,
)

class PasswordResetViewModel(
    private val authApi: AuthApi,
) : ViewModel() {
    private val _viewState = MutableStateFlow(PasswordResetViewState())

    val viewState = _viewState
        .asStateFlow()

    fun onResetPassword() = viewModelScope.launch {
        _viewState.update {
            it.copy(
                isLoading = true,
            )
        }

        authApi.sendPasswordResetEmail(_viewState.value.email)
            .onSuccess {
                _viewState.update {
                    it.copy(
                        isLoading = false,
                        isEmailSent = true,
                    )
                }
            }
            .onFailure {
                _viewState.update {
                    it.copy(
                        isLoading = false,
                        isEmailSent = false,
                    )
                }
            }
    }

    fun onEmailChange(email: String) {
        val isEmailValid = isEmailValid(email)

        _viewState.update {
            it.copy(
                email = email,
                isEmailValid = isEmailValid,
                inputErrorMessage = if (!isEmailValid) "Please enter a valid email" else null,
            )
        }
    }

    fun consumeTransientState() = _viewState.update {
        it.copy(
            isEmailSent = null,
        )
    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return email.matches(emailRegex.toRegex())
    }
}
