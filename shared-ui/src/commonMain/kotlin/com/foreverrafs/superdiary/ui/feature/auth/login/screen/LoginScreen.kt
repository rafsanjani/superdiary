package com.foreverrafs.superdiary.ui.feature.auth.login.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.foreverrafs.superdiary.core.utils.localActivityWrapper
import com.foreverrafs.superdiary.ui.feature.auth.login.LoginScreenViewModel
import com.foreverrafs.superdiary.ui.home.BottomNavigationScreen
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
object LoginScreen {
    @Composable
    fun Content(
        navController: NavHostController,
        onRegisterClick: () -> Unit,
    ) {
        val screenModel: LoginScreenViewModel = koinInject()
        val signInStatus by screenModel.viewState.collectAsStateWithLifecycle(
            initialValue = LoginViewState.Idle,
        )
        val activityWrapper = localActivityWrapper()
        val scope = rememberCoroutineScope()

        LoginScreenContent(
            viewState = signInStatus,
            onLoginClick = screenModel::onLoginClick,
            onLoginWithGoogle = {
                scope.launch {
                    activityWrapper?.let(screenModel::signInWithGoogle)
                }
            },
            onRegisterClick = onRegisterClick,
            onSignInSuccess = {
                navController.navigate(BottomNavigationScreen)
            },
        )
    }
}
