package com.foreverrafs.superdiary.auth.register.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foreverrafs.superdiary.auth.register.RegisterScreenViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterScreen(
    onLoginClick: () -> Unit,
    onRegisterSuccess: () -> Unit,
) {
    val screenModel: RegisterScreenViewModel = koinViewModel()
    val signInStatus by screenModel.viewState.collectAsStateWithLifecycle(
        initialValue = RegisterScreenState.Idle,
    )

    RegisterScreenContent(
        viewState = signInStatus,
        onRegisterClick = screenModel::onRegisterClick,
        onRegisterSuccess = onRegisterSuccess,
        onLoginClick = onLoginClick,
        onFieldChange = screenModel::onFieldChanged,
    )
}
