package com.foreverrafs.superdiary.ui.feature.auth.register.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.foreverrafs.superdiary.ui.feature.auth.login.screen.LoginScreen
import com.foreverrafs.superdiary.ui.feature.auth.register.RegisterScreenViewModel
import com.foreverrafs.superdiary.ui.home.BottomNavigationScreen
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
object RegisterScreen {
    @Composable
    fun Content(
        navController: NavHostController,
    ) {
        val screenModel: RegisterScreenViewModel = koinInject()
        val signInStatus by screenModel.viewState.collectAsStateWithLifecycle(
            initialValue = RegisterScreenState.Idle,
        )

        RegisterScreenContent(
            viewState = signInStatus,
            onRegisterClick = screenModel::onRegisterClick,
            onRegisterSuccess = {
                navController.navigate(BottomNavigationScreen)
            },
            onLoginClick = {
                navController.navigate(LoginScreen) {
                    popUpTo(LoginScreen) {
                        inclusive = true
                    }
                }
            },
        )
    }
}
