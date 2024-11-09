package com.foreverrafs.superdiary.ui.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.core.utils.ActivityWrapper
import com.foreverrafs.superdiary.core.utils.AppCoroutineDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginScreenViewModel(
    private val authApi: AuthApi,
    private val coroutineDispatchers: AppCoroutineDispatchers,
    private val logger: AggregateLogger,
) : ViewModel() {
    private val _viewState: MutableStateFlow<LoginViewState> =
        MutableStateFlow(LoginViewState.Initialized)

    val viewState = _viewState
        .asStateFlow()
        .onStart {
            restoreSession()
        }

    private fun restoreSession() = viewModelScope.launch(coroutineDispatchers.main) {
        authApi.restoreSession()
    }

    fun signInWithGoogle(activityWrapper: ActivityWrapper) =
        viewModelScope.launch(coroutineDispatchers.main) {
            _viewState.update {
                LoginViewState.Processing
            }

            when (val result = authApi.signInWithGoogle(activityWrapper)) {
                is AuthApi.SignInStatus.Error -> _viewState.update {
                    LoginViewState.Error(result.exception)
                }

                is AuthApi.SignInStatus.LoggedIn -> _viewState.update {
                    authApi.saveSession()
                    LoginViewState.Success
                }
            }
        }

    fun signInWithGoogle(token: String) = viewModelScope.launch(coroutineDispatchers.main) {
        _viewState.update {
            LoginViewState.Processing
        }
        when (val result = authApi.signInWithGoogle(token)) {
            is AuthApi.SignInStatus.Error -> _viewState.update {
                LoginViewState.Error(result.exception)
            }

            is AuthApi.SignInStatus.LoggedIn -> _viewState.update {
                logger.d(TAG) {
                    "Successfully logged in with Google"
                }
                saveLoginToken()
                LoginViewState.Success
            }
        }
    }

    private suspend fun saveLoginToken() {
        logger.d(TAG) {
            "Saving Google auth token"
        }
        authApi.saveSession()
    }

    companion object {
        private const val TAG = "LoginScreenViewModel"
    }
}
