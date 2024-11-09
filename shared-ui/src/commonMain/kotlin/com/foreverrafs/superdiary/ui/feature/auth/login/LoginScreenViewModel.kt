package com.foreverrafs.superdiary.ui.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.superdiary.core.utils.ActivityWrapper
import com.foreverrafs.superdiary.core.utils.AppCoroutineDispatchers
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

    fun signInWithGoogle(activityWrapper: ActivityWrapper?) =
        viewModelScope.launch(coroutineDispatchers.main) {
            _viewState.update {
                LoginViewState.Processing
            }

            when (val result = authApi.signInWithGoogle(activityWrapper)) {
                is AuthApi.SignInStatus.Error -> _viewState.update {
                    LoginViewState.Error(
                        error = result.exception,
                    )
                }

                is AuthApi.SignInStatus.LoggedIn -> _viewState.update {
                    LoginViewState.Success
                }
            }
        }
}
