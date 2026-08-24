package com.foreverrafs.superdiary.auth.login.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.auth.rememberAuthUiContext
import com.foreverrafs.superdiary.auth.login.LoginScreenViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    onResetPasswordClick: () -> Unit,
    onLoginSuccess: (UserInfo) -> Unit,
    isFromDeeplink: Boolean,
) {
    val screenModel: LoginScreenViewModel = koinViewModel()
    val authUiContext = rememberAuthUiContext()
    val signInStatus by screenModel.viewState.collectAsStateWithLifecycle(
        initialValue = LoginViewState.Idle,
    )

    LoginScreenContent(
        viewState = signInStatus,
        onLoginClick = screenModel::onLoginWithEmail,
        onLoginWithGoogle = { screenModel.onLoginWithGoogle(authUiContext) },
        onRegisterClick = onRegisterClick,
        onSignInSuccess = onLoginSuccess,
        isFromDeeplink = isFromDeeplink,
        onResetPasswordClick = onResetPasswordClick,
    )
}
