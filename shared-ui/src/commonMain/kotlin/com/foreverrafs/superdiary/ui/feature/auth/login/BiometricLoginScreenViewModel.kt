package com.foreverrafs.superdiary.ui.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.auth.BiometricAuth
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface BiometricLoginViewState {
    data object Success : BiometricLoginViewState
    data class Error(val exception: Exception) : BiometricLoginViewState
    data object Idle : BiometricLoginViewState
}

class BiometricLoginScreenViewModel(
    private val biometricAuth: BiometricAuth,
    private val logger: AggregateLogger,
) : ViewModel() {
    private val _viewState: MutableStateFlow<BiometricLoginViewState> =
        MutableStateFlow(BiometricLoginViewState.Idle)

    val viewState = _viewState
        .asStateFlow()
        .onStart {
            onAuthenticateWithBiometrics()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BiometricLoginViewState.Idle,
        )

    private fun onAuthenticateWithBiometrics() = viewModelScope.launch {
        authenticateWithBiometrics()
    }

    private suspend fun authenticateWithBiometrics() {
        if (!biometricAuth.canAuthenticate()) {
            logger.i(Tag) {
                "Biometric authentication is not available"
            }
            _viewState.update {
                BiometricLoginViewState.Error(
                    Exception("Biometric authentication is not available"),
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
            }

            is BiometricAuth.AuthResult.Failed -> {
                _viewState.update {
                    BiometricLoginViewState.Error(
                        Exception("error logging with biometrics"),
                    )
                }
            }

            is BiometricAuth.AuthResult.Success -> {
                _viewState.update {
                    BiometricLoginViewState.Success
                }
            }
        }
    }

    companion object {
        private val Tag = BiometricLoginScreenViewModel::class.simpleName.orEmpty()
    }
}
