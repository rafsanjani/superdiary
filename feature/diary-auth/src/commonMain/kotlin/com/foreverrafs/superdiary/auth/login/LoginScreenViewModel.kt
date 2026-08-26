package com.foreverrafs.superdiary.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.auth.AuthException
import com.foreverrafs.auth.AuthUiContext
import com.foreverrafs.auth.GenericAuthException
import com.foreverrafs.superdiary.auth.login.screen.LoginViewState
import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginScreenViewModel(
    private val authApi: AuthApi,
    private val coroutineDispatchers: AppCoroutineDispatchers,
) : ViewModel() {
    private val _viewState: MutableStateFlow<LoginViewState> =
        MutableStateFlow(LoginViewState.Idle)

    val viewState = _viewState
        .asStateFlow()

    fun onLoginWithGoogle(uiContext: AuthUiContext) =
        viewModelScope.launch(coroutineDispatchers.main) {
            _viewState.update {
                LoginViewState.Processing
            }

            val result = try {
                authApi.signInWithGoogle(uiContext)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                AuthApi.SessionStatus.Unauthenticated(
                    GenericAuthException(cause = e, message = e.message),
                )
            }

            when (result) {
                is AuthApi.SessionStatus.Unauthenticated -> _viewState.update {
                    LoginViewState.Error(
                        error = AuthException(cause = result.exception),
                    )
                }

                is AuthApi.SessionStatus.Authenticated -> _viewState.update { currentState ->
                    result.sessionInfo.userInfo?.let {
                        LoginViewState.Success(it)
                    } ?: currentState
                }
            }
        }

    fun onLoginWithEmail(username: CharSequence, password: CharSequence) =
        viewModelScope.launch(coroutineDispatchers.main) {
            _viewState.update {
                LoginViewState.Processing
            }

            when (val result = authApi.signIn(username.toString(), password.toString())) {
                is AuthApi.SessionStatus.Unauthenticated -> _viewState.update {
                    LoginViewState.Error(
                        error = AuthException(cause = result.exception),
                    )
                }

                is AuthApi.SessionStatus.Authenticated -> _viewState.update { currentState ->
                    result.sessionInfo.userInfo?.let {
                        LoginViewState.Success(it)
                    } ?: currentState
                }
            }
        }
}
