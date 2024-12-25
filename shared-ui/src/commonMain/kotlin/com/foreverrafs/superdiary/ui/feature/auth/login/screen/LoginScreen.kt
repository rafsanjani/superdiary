package com.foreverrafs.superdiary.ui.feature.auth.login.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.core.utils.localActivityWrapper
import com.foreverrafs.superdiary.ui.feature.auth.login.LoginScreenViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    onLoginSuccess: (UserInfo) -> Unit,
) {
    val screenModel: LoginScreenViewModel = koinInject()
    val signInStatus by screenModel.viewState.collectAsStateWithLifecycle(
        initialValue = LoginViewState.Idle,
    )
    val activityWrapper = localActivityWrapper()
    val scope = rememberCoroutineScope()

    LoginScreen(
        viewState = signInStatus,
        onLoginClick = screenModel::onLoginClick,
        onLoginWithGoogle = {
            scope.launch {
                activityWrapper?.let(screenModel::signInWithGoogle)
            }
        },
        onRegisterClick = onRegisterClick,
        onSignInSuccess = onLoginSuccess,
    )
}
