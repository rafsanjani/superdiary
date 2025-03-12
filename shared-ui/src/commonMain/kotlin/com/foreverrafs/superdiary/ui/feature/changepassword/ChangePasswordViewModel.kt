package com.foreverrafs.superdiary.ui.feature.changepassword

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ChangePasswordViewModel(
    private val logger: AggregateLogger,
    private val authApi: AuthApi,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    data class ChangePasswordScreenState(
        val passwordStrength: PasswordStrength? = null,
        val arePasswordsMatching: Boolean? = null,
        val errorMessage: String? = null,
        val isProcessing: Boolean? = false,
        val isSuccess: Boolean? = null,
    )

    private val _viewState: MutableStateFlow<ChangePasswordScreenState> =
        MutableStateFlow(ChangePasswordScreenState())

    val viewState = _viewState.asStateFlow()

    sealed interface ChangePasswordScreenAction {
        data class PasswordValueChange(val value: String) : ChangePasswordScreenAction
        data class ConfirmPasswordValueChange(val value: String) : ChangePasswordScreenAction
        data object SubmitPasswordChange : ChangePasswordScreenAction
        data object DismissErrorMessage : ChangePasswordScreenAction
    }

    fun onAction(action: ChangePasswordScreenAction) = when (action) {
        is ChangePasswordScreenAction.PasswordValueChange -> {
            savedStateHandle[PASSWORD_KEY] = action.value
            checkPasswordStrength()
        }

        is ChangePasswordScreenAction.ConfirmPasswordValueChange -> {
            savedStateHandle[CONFIRM_PASSWORD_KEY] = action.value
            checkPasswordEquality()
        }

        is ChangePasswordScreenAction.SubmitPasswordChange -> submit()
        is ChangePasswordScreenAction.DismissErrorMessage -> dismissErrorMessage()
    }

    private fun dismissErrorMessage() {
        _viewState.update {
            it.copy(errorMessage = null)
        }
    }

    private fun checkPasswordEquality() {
        logger.i(TAG) {
            "Checking if password and confirmation are equal"
        }
        val password = savedStateHandle.get<String>(PASSWORD_KEY) ?: ""
        val confirmPassword = savedStateHandle.get<String>(CONFIRM_PASSWORD_KEY) ?: ""

        if (password.isEmpty() || confirmPassword.isEmpty()) {
            logger.i(TAG) {
                "Skipping password equality check because one or all the fields is empty"
            }
            return
        }

        _viewState.update {
            it.copy(arePasswordsMatching = password == confirmPassword)
        }
    }

    private fun checkPasswordStrength() {
        logger.i(TAG) {
            "checking password strength"
        }
        val password = savedStateHandle.get<String>(PASSWORD_KEY) ?: ""

        if (password.isEmpty()) {
            logger.i(TAG) {
                "Skipping password strength check for empty password"
            }
            _viewState.update {
                it.copy(passwordStrength = null)
            }
            return
        }

        val lengthCriteria = password.length >= 8
        val uppercaseCriteria = password.any { it.isUpperCase() }
        val lowercaseCriteria = password.any { it.isLowerCase() }
        val digitCriteria = password.any { it.isDigit() }
        val specialCharCriteria = password.any { it in "!@#$%^&*()-_=+{}[]|:;'<>,.?/" }

        val passwordStrength = when {
            // strong password, meets all criteria
            lengthCriteria && uppercaseCriteria && lowercaseCriteria && digitCriteria && specialCharCriteria -> {
                PasswordStrength.Strong
            }

            // medium password, meets length and at least two other criteria
            lengthCriteria && ((uppercaseCriteria && lowercaseCriteria) || (digitCriteria && specialCharCriteria)) -> {
                PasswordStrength.Medium
            }

            // weak password
            else -> PasswordStrength.Weak
        }

        _viewState.update { currentState ->
            currentState.copy(
                passwordStrength = passwordStrength,
            )
        }
    }

    /**
     * Submit password change request. The password should already be in the SavedStateHandle by
     * the time we get to this point so extra checks are not needed
     */
    private fun submit() = viewModelScope.launch {
        val password = savedStateHandle.get<String>(PASSWORD_KEY) ?: ""

        logger.i(TAG) {
            "submitting password change request"
        }

        _viewState.update {
            it.copy(isProcessing = true)
        }

        authApi.updatePassword(password)
            .onSuccess {
                logger.i(TAG) { "Password successfully updated!" }
                _viewState.update {
                    it.copy(
                        isSuccess = true,
                    )
                }
            }
            .onFailure {
                // set the error message
                _viewState.update {
                    it.copy(
                        errorMessage = "Error submitting password change request.",
                        arePasswordsMatching = null,
                        passwordStrength = null,
                        isProcessing = false,
                    )
                }
                logger.e(TAG, it) {
                    "Error updating password"
                }
            }
    }

    companion object {
        private const val TAG = "ChangePasswordViewModel"
        private const val PASSWORD_KEY = "password"
        private const val CONFIRM_PASSWORD_KEY = "confirm_password"
    }
}

enum class PasswordStrength {
    None,
    Weak,
    Medium,
    Strong,
}
