package com.foreverrafs.superdiary.ui.feature.changepassword

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.foreverrafs.superdiary.ui.animatedComposable
import com.foreverrafs.superdiary.ui.feature.changepassword.screen.ChangePasswordScreen
import com.foreverrafs.superdiary.ui.feature.changepassword.screen.ChangePasswordSuccessScreen
import com.foreverrafs.superdiary.ui.navigation.AppRoute
import kotlinx.serialization.Serializable

@Composable
fun ChangePasswordNavHost(
    rootNavController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = ChangePasswordRoute.ChangePasswordScreen,
        modifier = modifier,
    ) {
        animatedComposable<ChangePasswordRoute.ChangePasswordScreen> {
            ChangePasswordScreen(
                onPasswordChangeSuccess = {
                    navController.navigate(ChangePasswordRoute.PasswordChangeSuccessScreen) {
                        popUpTo(ChangePasswordRoute.ChangePasswordScreen) {
                            inclusive = true
                        }
                    }
                },
            )
        }

        animatedComposable<ChangePasswordRoute.PasswordChangeSuccessScreen> {
            ChangePasswordSuccessScreen(
                onPrimaryButtonClick = {
                    rootNavController.navigate(AppRoute.BottomNavigationScreen(null)) {
                        popUpTo(rootNavController.graph.startDestinationRoute.orEmpty()) {
                            inclusive = true
                        }
                    }
                },
            )
        }
    }
}

sealed interface ChangePasswordRoute {
    @Serializable
    data object ChangePasswordScreen : ChangePasswordRoute

    @Serializable
    data object PasswordChangeSuccessScreen : ChangePasswordRoute
}
