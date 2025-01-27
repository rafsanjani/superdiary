package com.foreverrafs.superdiary.auth.register.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foreverrafs.superdiary.auth.register.RegisterScreenViewModel
import com.foreverrafs.superdiary.design.components.BackHandler
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

    BackHandler {
        // Prevent user from navigating back from this screen
    }

    RegisterScreenContent(
        viewState = signInStatus,
        onRegisterClick = screenModel::onRegisterClick,
        onRegisterSuccess = onRegisterSuccess,
        onLoginClick = onLoginClick,
    )
}
