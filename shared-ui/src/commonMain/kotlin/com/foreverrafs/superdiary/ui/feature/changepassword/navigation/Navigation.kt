package com.foreverrafs.superdiary.ui.feature.changepassword.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.foreverrafs.superdiary.auth.reset.SendPasswordResetEmailScreen
import com.foreverrafs.superdiary.ui.feature.changepassword.screen.ChangePasswordScreen
import com.foreverrafs.superdiary.ui.feature.changepassword.screen.ChangePasswordSuccessScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
internal fun EntryProviderScope<NavKey>.ChangePasswordNavigation(
    onPasswordChangeComplete: () -> Unit,
    onBackPress: () -> Unit,
    requiresNewPassword: Boolean,
) {
    val startDestination = if (requiresNewPassword) {
        ChangePasswordRoute.ChangePasswordScreen
    } else {
        ChangePasswordRoute.SendPasswordResetEmailScreen
    }

    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(
                        subclass = ChangePasswordRoute.ChangePasswordScreen::class,
                        serializer = ChangePasswordRoute.ChangePasswordScreen.serializer(),
                    )
                    subclass(
                        subclass = ChangePasswordRoute.PasswordChangeSuccessScreen::class,
                        serializer = ChangePasswordRoute.PasswordChangeSuccessScreen.serializer(),
                    )

                    subclass(
                        subclass = ChangePasswordRoute.SendPasswordResetEmailScreen::class,
                        serializer = ChangePasswordRoute.SendPasswordResetEmailScreen.serializer(),
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
            entry<ChangePasswordRoute.ChangePasswordScreen> { key ->
                ChangePasswordScreen(
                    onPasswordChangeSuccess = {
                        backStack.clear()
                        backStack.add(ChangePasswordRoute.PasswordChangeSuccessScreen)
                    },
                )
            }
            entry<ChangePasswordRoute.PasswordChangeSuccessScreen> {
                ChangePasswordSuccessScreen(
                    onContinueClick = onPasswordChangeComplete,
                )
            }

            entry<ChangePasswordRoute.SendPasswordResetEmailScreen> {
                SendPasswordResetEmailScreen()
            }
        },
    )
}
