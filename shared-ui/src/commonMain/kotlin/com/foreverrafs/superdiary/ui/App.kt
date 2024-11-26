package com.foreverrafs.superdiary.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.ui.feature.auth.login.screen.LoginScreen
import com.foreverrafs.superdiary.ui.feature.auth.register.screen.RegisterScreen
import com.foreverrafs.superdiary.ui.feature.creatediary.screen.CreateDiaryScreen
import com.foreverrafs.superdiary.ui.feature.details.screen.DetailScreen
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListScreen
import com.foreverrafs.superdiary.ui.home.BottomNavigationScreen
import com.foreverrafs.superdiary.ui.style.SuperdiaryTheme
import okio.FileSystem
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import superdiary.shared_ui.generated.resources.Res
import superdiary.shared_ui.generated.resources.logo

/**
 * Entry point into the whole app. In an ideal world we'll only just render
 * this composable on individual platforms but the world isn't ideal, huh!
 */

@Composable
fun App(modifier: Modifier = Modifier) {
    val appViewModel: AppViewModel = koinInject()
    val appViewState by appViewModel.viewState.collectAsStateWithLifecycle()

    SuperdiaryTheme {
        setSingletonImageLoaderFactory(::getAsyncImageLoader)

        when (appViewState) {
            is AppSessionState.Processing -> {
                SuperdiaryTheme {
                    Surface {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
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
                                modifier = Modifier
                                    .size(96.dp)
                                    .graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                    },
                                painter = painterResource(Res.drawable.logo),
                                contentDescription = null,
                            )
                        }
                    }
                }
                return@SuperdiaryTheme
            }

            is AppSessionState.Error, AppSessionState.UnAuthenticated,
            -> SuperDiaryNavHost(
                modifier = modifier,
                isSignedIn = false,
            )

            is AppSessionState.Success -> SuperDiaryNavHost(
                modifier = modifier,
                isSignedIn = true,
                userInfo = (appViewState as AppSessionState.Success).userInfo,
            )
        }
    }
}

@Composable
private fun SuperDiaryNavHost(
    isSignedIn: Boolean,
    modifier: Modifier = Modifier,
    userInfo: UserInfo? = null,
) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = if (isSignedIn) BottomNavigationScreen else LoginScreen,
    ) {
        animatedComposable<LoginScreen> {
            LoginScreen.Content(
                onLoginSuccess = {
                    navController.navigate(BottomNavigationScreen) {
                        popUpTo(BottomNavigationScreen) {
                            inclusive = true
                        }
                    }
                },
                onRegisterClick = {
                    navController.navigate(RegisterScreen)
                },
            )
        }

        animatedComposable<RegisterScreen> {
            RegisterScreen.Content(
                navController = navController,
            )
        }

        animatedComposable<BottomNavigationScreen> {
            BottomNavigationScreen.Content(
                rootNavController = navController,
            )
        }

        composable<CreateDiaryScreen> {
            CreateDiaryScreen.Content(
                navController = navController,
                userInfo = userInfo,
            )
        }

        animatedComposable<DiaryListScreen> {
            DiaryListScreen.Content(
                navController = navController,
                userInfo = userInfo,
            )
        }

        animatedComposable<DetailScreen> { backstackEntry ->
            val diaryId: String? = backstackEntry.arguments?.getString("diaryId")

            diaryId?.let {
                DetailScreen.Content(
                    diaryId = diaryId,
                    navController = navController,
                )
            }
        }
    }
}

private inline fun <reified T : Any> NavGraphBuilder.animatedComposable(
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) = composable<T>(
    content = content,
    enterTransition = { enterTransition() },
    exitTransition = { exitTransition() },
    popEnterTransition = { enterTransition() },
    popExitTransition = { exitTransition() },
)

private fun enterTransition() = fadeIn(
    animationSpec = tween(
        durationMillis = 300,
        easing = LinearEasing,
    ),
)

private fun exitTransition() = fadeOut(
    animationSpec = tween(
        durationMillis = 300,
        easing = LinearEasing,
    ),
)

fun getAsyncImageLoader(context: PlatformContext) =
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

fun newDiskCache(): DiskCache =
    DiskCache.Builder()
        .directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "image_cache")
        .maxSizeBytes(1024L * 1024 * 1024) // 512MB
        .build()
