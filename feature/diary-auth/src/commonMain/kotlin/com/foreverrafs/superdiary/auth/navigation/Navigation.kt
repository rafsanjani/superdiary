package com.foreverrafs.superdiary.auth.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.auth.changepassword.screen.ChangePasswordScreen
import com.foreverrafs.superdiary.auth.changepassword.screen.ChangePasswordSuccessScreen
import com.foreverrafs.superdiary.auth.login.screen.BiometricAuthScreen
import com.foreverrafs.superdiary.auth.login.screen.LoginScreen
import com.foreverrafs.superdiary.auth.register.screen.RegisterScreen
import com.foreverrafs.superdiary.auth.register.screen.RegistrationConfirmationScreen
import com.foreverrafs.superdiary.auth.reset.SendPasswordResetEmailScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun EntryProviderScope<NavKey>.AuthNavigation(
    onPasswordChangeComplete: () -> Unit,
    onAuthenticationSuccess: (UserInfo?) -> Unit,
    onBackPress: () -> Unit,
    requiresNewPassword: Boolean,
    isFromDeepLink: Boolean,
    showLoginScreen: Boolean,
    showBiometricAuth: Boolean,
) {
    val startDestination = when {
        requiresNewPassword -> AuthRoute.ChangePasswordScreen
        showBiometricAuth -> AuthRoute.BiometricAuthScreen
        showLoginScreen -> AuthRoute.LoginScreen(isFromDeepLink = isFromDeepLink)
        else -> AuthRoute.LoginScreen(isFromDeepLink = isFromDeepLink)
    }

    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(
                        subclass = AuthRoute.ChangePasswordScreen::class,
                        serializer = AuthRoute.ChangePasswordScreen.serializer(),
                    )

                    subclass(
                        subclass = AuthRoute.PasswordChangeSuccessScreen::class,
                        serializer = AuthRoute.PasswordChangeSuccessScreen.serializer(),
                    )

                    subclass(
                        subclass = AuthRoute.SendPasswordResetEmailScreen::class,
                        serializer = AuthRoute.SendPasswordResetEmailScreen.serializer(),
                    )

                    subclass(
                        subclass = AuthRoute.BiometricAuthScreen::class,
                        serializer = AuthRoute.BiometricAuthScreen.serializer(),
                    )

                    subclass(
                        subclass = AuthRoute.RegistrationConfirmationScreen::class,
                        serializer = AuthRoute.RegistrationConfirmationScreen.serializer(),
                    )

                    subclass(
                        subclass = AuthRoute.LoginScreen::class,
                        serializer = AuthRoute.LoginScreen.serializer(),
                    )

                    subclass(
                        subclass = AuthRoute.RegisterScreen::class,
                        serializer = AuthRoute.RegisterScreen.serializer(),
                    )
                }
            }
        },
        startDestination,
    )

    NavDisplay(
        backStack = backStack,
        onBack = {
            if (backStack.isNotEmpty()) {
                backStack.removeAt(backStack.lastIndex)
            } else {
                onBackPress()
            }
        },
        entryDecorators = listOf<NavEntryDecorator<NavKey>>(
            rememberSaveableStateHolderNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<AuthRoute.ChangePasswordScreen> { key ->
                ChangePasswordScreen(
                    onPasswordChangeSuccess = {
                        backStack.clear()
                        backStack.add(AuthRoute.PasswordChangeSuccessScreen)
                    },
                )
            }
            entry<AuthRoute.PasswordChangeSuccessScreen> {
                ChangePasswordSuccessScreen(
                    onContinueClick = onPasswordChangeComplete,
                )
            }

            entry<AuthRoute.SendPasswordResetEmailScreen> {
                SendPasswordResetEmailScreen()
            }

            entry<AuthRoute.BiometricAuthScreen> {
                BiometricAuthScreen(
                    onBiometricAuthSuccess = {
                        onAuthenticationSuccess(null)
                    },
                )
            }

            entry<AuthRoute.LoginScreen> { key ->
                LoginScreen(
                    onRegisterClick = {
                        backStack.add(AuthRoute.RegisterScreen)
                    },
                    onResetPasswordClick = {
                        backStack.add(AuthRoute.SendPasswordResetEmailScreen)
                    },
                    onLoginSuccess = {
                        onAuthenticationSuccess(it)
                    },
                    isFromDeeplink = key.isFromDeepLink,
                )
            }

            entry<AuthRoute.RegisterScreen> {
                RegisterScreen(
                    onLoginClick = {
                        backStack.removeAt(backStack.lastIndex)
                    },
                    onRegisterSuccess = {
                        backStack.removeAt(backStack.lastIndex)
                        backStack.add(
                            AuthRoute.RegistrationConfirmationScreen,
                        )
                    },
                )
            }

            entry<AuthRoute.RegistrationConfirmationScreen> {
                RegistrationConfirmationScreen()
            }
        },
    )
}
