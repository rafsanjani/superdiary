package com.foreverrafs.superdiary.ui.feature.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.superdiary.core.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.ui.feature.auth.register.screen.RegisterScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterScreenViewModel(
    private val authApi: AuthApi,
    private val coroutineDispatchers: AppCoroutineDispatchers,
) : ViewModel() {
    private val _viewState: MutableStateFlow<RegisterScreenState> =
        MutableStateFlow(RegisterScreenState.Idle)

    val viewState = _viewState
        .asStateFlow()

    fun onRegisterClick(
        name: String,
        email: String,
        password: String,
    ) =
        viewModelScope.launch(coroutineDispatchers.main) {
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
