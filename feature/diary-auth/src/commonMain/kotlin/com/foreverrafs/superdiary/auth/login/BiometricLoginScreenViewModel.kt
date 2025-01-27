package com.foreverrafs.superdiary.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.auth.BiometricAuth
import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface BiometricLoginScreenState {
    data object Success : BiometricLoginScreenState
    data class Error(val exception: Exception) : BiometricLoginScreenState
    data object Idle : BiometricLoginScreenState
}

class BiometricAuthUnavailableException : Exception()
class BiometricAuthenticationException : Exception()

class BiometricLoginScreenViewModel(
    private val biometricAuth: BiometricAuth,
    private val logger: AggregateLogger,
    private val coroutineDispatchers: AppCoroutineDispatchers,
) : ViewModel() {
    private val _viewState: MutableStateFlow<BiometricLoginScreenState> =
        MutableStateFlow(BiometricLoginScreenState.Idle)

    val viewState = _viewState
        .asStateFlow()
        .onStart {
            onAuthenticateWithBiometrics()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BiometricLoginScreenState.Idle,
        )

    private fun onAuthenticateWithBiometrics() = viewModelScope.launch(coroutineDispatchers.main) {
        authenticateWithBiometrics()
    }

    private suspend fun authenticateWithBiometrics() {
        if (!biometricAuth.canAuthenticate()) {
            logger.i(Tag) {
                "Biometric authentication is not available"
            }
            _viewState.update {
                BiometricLoginScreenState.Error(
                    BiometricAuthUnavailableException(),
                )
            }
            return
        }

        when (val biometricAuthResult = biometricAuth.startBiometricAuth()) {
            is BiometricAuth.AuthResult.Error -> {
                logger.e(
                    tag = Tag,
                    throwable = biometricAuthResult.error,
                ) {
                    "Error performing biometric authentication"
                }

                _viewState.update {
                    BiometricLoginScreenState.Error(
                        BiometricAuthenticationException(),
                    )
                }
            }

            is BiometricAuth.AuthResult.Failed -> {
                logger.e(tag = Tag) {
                    "Error performing biometric authentication"
                }
                _viewState.update {
                    BiometricLoginScreenState.Error(
                        BiometricAuthenticationException(),
                    )
                }
            }

            is BiometricAuth.AuthResult.Success -> {
                _viewState.update {
                    BiometricLoginScreenState.Success
                }
            }
        }
    }

    companion object {
        private val Tag = BiometricLoginScreenViewModel::class.simpleName.orEmpty()
    }
}
