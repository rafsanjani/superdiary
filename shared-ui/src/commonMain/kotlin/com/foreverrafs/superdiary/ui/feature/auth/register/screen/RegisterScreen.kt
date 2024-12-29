package com.foreverrafs.superdiary.ui.feature.auth.register.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.foreverrafs.superdiary.ui.feature.auth.register.RegisterScreenViewModel
import com.foreverrafs.superdiary.ui.navigation.AppRoute
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterScreenContent(
    navController: NavHostController,
) {
    val screenModel: RegisterScreenViewModel = koinViewModel()
    val signInStatus by screenModel.viewState.collectAsStateWithLifecycle(
        initialValue = RegisterScreenState.Idle,
    )

    RegisterScreenContent(
        viewState = signInStatus,
        onRegisterClick = screenModel::onRegisterClick,
        onRegisterSuccess = {
            navController.navigate(AppRoute.RegistrationConfirmationScreen) {
                popUpTo(AppRoute.RegistrationConfirmationScreen) {
                    inclusive = true
                }
            }
        },
        onLoginClick = {
            navController.navigate(AppRoute.LoginScreen) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        },
    )
}
