package com.foreverrafs.superdiary.ui.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import com.foreverrafs.superdiary.auth.login.screen.BiometricAuthScreen
import com.foreverrafs.superdiary.auth.login.screen.LoginScreen
import com.foreverrafs.superdiary.auth.register.DeeplinkContainer
import com.foreverrafs.superdiary.auth.register.screen.RegisterScreen
import com.foreverrafs.superdiary.auth.register.screen.RegistrationConfirmationScreen
import com.foreverrafs.superdiary.design.style.LocalSharedTransitionScope
import com.foreverrafs.superdiary.design.style.SuperDiaryTheme
import com.foreverrafs.superdiary.list.navigation.DiaryListNavigation
import com.foreverrafs.superdiary.list.navigation.DiaryListRoute
import com.foreverrafs.superdiary.profile.presentation.screen.ProfileScreen
import com.foreverrafs.superdiary.ui.AppSessionState
import com.foreverrafs.superdiary.ui.feature.changepassword.navigation.ChangePasswordNavigation
import com.foreverrafs.superdiary.ui.feature.creatediary.screen.CreateDiaryScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import okio.FileSystem
import org.jetbrains.compose.resources.painterResource
import superdiary.shared_ui.generated.resources.Res
import superdiary.shared_ui.generated.resources.logo

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun SuperDiaryNavHost(
    viewState: AppSessionState,
    modifier: Modifier = Modifier,
) {
    // This userInfo is used when a session is automatically restored after app is launched.
    val userInfo by remember(viewState) {
        mutableStateOf((viewState as? AppSessionState.Authenticated)?.userInfo)
    }

    // App is attempting to load a session from storage. Show a loading screen
    if (viewState is AppSessionState.Processing) {
        LoadingScreen()
        return
    }

    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = navigationSerializersModule
        },
        getStartDestination(viewState),
    )

    SharedTransitionLayout(modifier = modifier) {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeAt(backStack.lastIndex) },
                entryDecorators = listOf<NavEntryDecorator<NavKey>>(
                    rememberSaveableStateHolderNavEntryDecorator(),
                ),
                entryProvider = entryProvider {
                    entry<AppRoute.BottomNavigationNavHost> { key ->
                        BottomNavigationScreen(
                            userInfo = key.userInfo,
                            onProfileClick = {
                                backStack.add(AppRoute.ProfileScreen)
                            },
                            onAddEntry = {
                                backStack.add(AppRoute.CreateDiaryScreen)
                            },
                            onSeeAll = {
                                backStack.add(AppRoute.DiaryListNavHost)
                            },
                            onDiaryClick = {
                                backStack.add(DiaryListRoute.DetailScreen(it.toString()))
                            },
                        )
                    }

                    entry<AppRoute.ProfileScreen> {
                        ProfileScreen(
                            onLogoutComplete = {
                                backStack.clear()
                                backStack.add(
                                    AppRoute.LoginScreen(isFromDeeplink = false),
                                )
                            },
                            onNavigateBack = { backStack.removeAt(backStack.lastIndex) },
                        )
                    }

                    entry<AppRoute.DiaryListNavHost> {
                        DiaryListNavigation(
                            onBackPress = { backStack.removeAt(backStack.lastIndex) },
                            onAddEntry = {
                                backStack.add(
                                    AppRoute.CreateDiaryScreen,
                                )
                            },
                            onProfileClick = {
                                backStack.add(
                                    AppRoute.ProfileScreen,
                                )
                            },
                        )
                    }

                    entry<AppRoute.BiometricAuthScreen> {
                        BiometricAuthScreen(
                            onBiometricAuthSuccess = {
                                backStack.add(
                                    AppRoute.BottomNavigationNavHost(userInfo = null),
                                )

                                backStack.remove(AppRoute.BiometricAuthScreen)
                            },
                        )
                    }

                    entry<AppRoute.LoginScreen> { key ->
                        LoginScreen(
                            onRegisterClick = {
                                backStack.add(AppRoute.RegisterScreen)
                            },
                            onResetPasswordClick = {
                                backStack.add(AppRoute.ChangePasswordNavHost())
                            },
                            onLoginSuccess = {
                                backStack.add(
                                    AppRoute.BottomNavigationNavHost(
                                        userInfo = it,
                                    ),
                                )
                            },
                            isFromDeeplink = key.isFromDeeplink,
                        )
                    }

                    entry<AppRoute.RegisterScreen> {
                        RegisterScreen(
                            onLoginClick = {
                                backStack.removeAt(backStack.lastIndex)
                            },
                            onRegisterSuccess = {
                                backStack.add(
                                    AppRoute.RegistrationConfirmationScreen,
                                )
                            },
                        )
                    }

                    entry<AppRoute.RegistrationConfirmationScreen> {
                        RegistrationConfirmationScreen()
                    }

                    entry<AppRoute.ChangePasswordNavHost> { key ->
                        ChangePasswordNavigation(
                            onPasswordChangeComplete = {
                                backStack.clear()
                                backStack.add(AppRoute.BottomNavigationNavHost(null))
                            },
                            onBackPress = { backStack.removeAt(backStack.lastIndex) },
                            requiresNewPassword = key.requiresNewPassword,
                        )
                    }

                    entry<AppRoute.CreateDiaryScreen> {
                        CreateDiaryScreen(
                            userInfo = userInfo,
                            onDiarySave = { backStack.removeAt(backStack.lastIndex) },
                            onNavigateBack = { backStack.removeAt(backStack.lastIndex) },
                        )
                    }
                },
            )
        }
    }
}

private val navigationSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(
            subclass = AppRoute.LoginScreen::class,
            serializer = AppRoute.LoginScreen.serializer(),
        )

        subclass(
            subclass = AppRoute.CreateDiaryScreen::class,
            serializer = AppRoute.CreateDiaryScreen.serializer(),
        )

        subclass(
            subclass = AppRoute.BottomNavigationNavHost::class,
            serializer = AppRoute.BottomNavigationNavHost.serializer(),
        )

        subclass(
            subclass = AppRoute.BiometricAuthScreen::class,
            serializer = AppRoute.BiometricAuthScreen.serializer(),
        )
    }
}

@Composable
fun getStartDestination(viewState: AppSessionState): NavKey = remember(viewState) {
    when (viewState) {
        is AppSessionState.Authenticated -> {
            when (viewState.linkType) {
                DeeplinkContainer.LinkType.EmailConfirmation,
                DeeplinkContainer.LinkType.MagicLink,
                DeeplinkContainer.LinkType.Registration,
                -> AppRoute.BottomNavigationNavHost(
                    viewState.userInfo,
                )

                DeeplinkContainer.LinkType.PasswordRecovery -> AppRoute.ChangePasswordNavHost(
                    requiresNewPassword = true,
                )

                DeeplinkContainer.LinkType.Invalid -> AppRoute.LoginScreen(isFromDeeplink = true)
                // Session was restored from disk and didn't originate from an email link
                null -> {
                    // If user has biometrics enabled, show a screen asking them for that, else go straight to bottom navigation screen
                    if (viewState.isBiometricAuthEnabled == true) {
                        AppRoute.BiometricAuthScreen
                    } else {
                        AppRoute.BottomNavigationNavHost(viewState.userInfo)
                    }
                }
            }
        }

        is AppSessionState.Error -> {
            AppRoute.LoginScreen(
                isFromDeeplink = viewState.isFromDeeplink,
            )
        }

        is AppSessionState.Processing,
        is AppSessionState.UnAuthenticated,
        -> AppRoute.LoginScreen(isFromDeeplink = false)
    }
}

@Composable
private fun LoadingScreen() {
    SuperDiaryTheme {
        Surface {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                val imageAnimation = rememberInfiniteTransition()

                val scale by imageAnimation.animateFloat(
                    initialValue = 1f,
                    targetValue = 0.65f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 1000,
                        ),
                        repeatMode = RepeatMode.Reverse,
                    ),
                )

                Image(
                    modifier = Modifier.size(96.dp).graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = null,
                )
            }
        }
    }
}

internal fun getAsyncImageLoader(context: PlatformContext) =
    ImageLoader.Builder(context)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .memoryCache {
            MemoryCache.Builder().maxSizePercent(context, 0.3).strongReferencesEnabled(true).build()
        }
        .diskCachePolicy(CachePolicy.ENABLED)
        .networkCachePolicy(CachePolicy.ENABLED)
        .diskCache {
            newDiskCache()
        }
        .crossfade(true)
        .build()

internal fun newDiskCache(): DiskCache =
    DiskCache.Builder().directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "image_cache")
        .maxSizeBytes(1024L * 1024 * 1024) // 512MB
        .build()
