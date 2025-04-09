package com.foreverrafs.superdiary.ui.feature.changepassword.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.foreverrafs.superdiary.ui.animatedComposable
import com.foreverrafs.superdiary.ui.feature.changepassword.screen.ChangePasswordScreen
import com.foreverrafs.superdiary.ui.feature.changepassword.screen.ChangePasswordSuccessScreen
import com.foreverrafs.superdiary.ui.navigation.AppRoute

internal inline fun <reified T : Any> NavGraphBuilder.changePasswordNavigation(
    navController: NavHostController,
) {
    navigation<T>(startDestination = ChangePasswordRoute.ChangePasswordScreen) {
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
                    navController.navigate(AppRoute.BottomNavigationScreen(null)) {
                        popUpTo(navController.graph.startDestinationRoute.orEmpty()) {
                            inclusive = true
                        }
                    }
                },
            )
        }
    }
}
