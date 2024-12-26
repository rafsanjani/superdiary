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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.core.bundle.Bundle
import androidx.core.uri.UriUtils
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.ui.feature.auth.login.screen.LoginScreen
import com.foreverrafs.superdiary.ui.feature.auth.register.screen.RegisterScreenContent
import com.foreverrafs.superdiary.ui.feature.auth.register.screen.RegistrationConfirmationScreen
import com.foreverrafs.superdiary.ui.feature.creatediary.screen.CreateDiaryScreen
import com.foreverrafs.superdiary.ui.feature.details.screen.DetailScreenContent
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListScreen
import com.foreverrafs.superdiary.ui.home.BottomNavigationScreen
import com.foreverrafs.superdiary.ui.navigation.AppRoute
import com.foreverrafs.superdiary.ui.style.SuperdiaryTheme
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlinx.serialization.json.Json
import okio.FileSystem
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import superdiary.shared_ui.generated.resources.Res
import superdiary.shared_ui.generated.resources.logo

/**
 * Entry point into the whole app. In an ideal world we'll only just render
 * this composable on individual platforms but the world isn't ideal, huh!
 */

@Composable
fun App(modifier: Modifier = Modifier) {
    val appViewModel: AppViewModel = koinViewModel()
    val appViewState by appViewModel.viewState.collectAsStateWithLifecycle()

    SuperdiaryTheme {
        setSingletonImageLoaderFactory(::getAsyncImageLoader)
        SuperDiaryNavHost(
            viewState = appViewState,
            modifier = modifier,
            onLogout = appViewModel::logOut,
        )
    }
}

@Composable
private fun SuperDiaryNavHost(
    viewState: AppSessionState,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSignedIn by remember(viewState) {
        mutableStateOf(viewState is AppSessionState.Success)
    }

    // This userInfo is used when a session is automatically restored after app is launched.
    val userInfo by remember(viewState) {
        mutableStateOf((viewState as? AppSessionState.Success)?.userInfo)
    }

    val navController = rememberNavController()

    // App is attempting to load a session from storage. Show a loading screen
    if (viewState is AppSessionState.Processing) {
        LoadingScreen()
        return
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = if (isSignedIn) {
            AppRoute.BottomNavigationScreen(userInfo)
        } else {
            AppRoute.LoginScreen
        },
    ) {
        animatedComposable<AppRoute.LoginScreen> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(
                        AppRoute.BottomNavigationScreen(
                            userInfo = it,
                        ),
                    ) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                },
                onRegisterClick = {
                    navController.navigate(AppRoute.RegisterScreen)
                },
            )
        }

        animatedComposable<AppRoute.RegisterScreen> {
            RegisterScreenContent(
                navController = navController,
            )
        }

        animatedComposable<AppRoute.RegistrationConfirmationScreen> {
            RegistrationConfirmationScreen()
        }

        animatedComposable<AppRoute.BottomNavigationScreen>(
            typeMap = mapOf(typeOf<UserInfo?>() to UserInfoNavType),
        ) {
            val route = it.toRoute<AppRoute.BottomNavigationScreen>()

            BottomNavigationScreen(
                rootNavController = navController,
                onLogout = onLogout,
                userInfo = route.userInfo,
            )
        }

        composable<AppRoute.CreateDiaryScreen> {
            CreateDiaryScreen(
                navController = navController,
                userInfo = userInfo,
            )
        }

        animatedComposable<AppRoute.DiaryListScreen> {
            DiaryListScreen(
                navController = navController,
                avatarUrl = userInfo?.avatarUrl,
            )
        }

        animatedComposable<AppRoute.DetailScreen> { backstackEntry ->
            val diaryId: String = backstackEntry.toRoute<AppRoute.DetailScreen>().diaryId

            DetailScreenContent(
                diaryId = diaryId,
                navController = navController,
                avatarUrl = userInfo?.avatarUrl.orEmpty(),
            )
        }
    }
}

@Composable
private fun LoadingScreen() {
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
}

private inline fun <reified T : Any> NavGraphBuilder.animatedComposable(
    typeMap: Map<KType, NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) = composable<T>(
    content = content,
    enterTransition = { enterTransition() },
    exitTransition = { exitTransition() },
    popEnterTransition = { enterTransition() },
    popExitTransition = { exitTransition() },
    typeMap = typeMap,
    deepLinks = deepLinks,
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

object UserInfoNavType : NavType<UserInfo?>(isNullableAllowed = true) {
    override fun get(bundle: Bundle, key: String): UserInfo? =
        bundle.getString(key)?.let { Json.decodeFromString(it) }

    override fun parseValue(value: String): UserInfo? {
        val decodedValue = UriUtils.decode(value)
        return Json.decodeFromString(decodedValue)
    }

    override fun put(bundle: Bundle, key: String, value: UserInfo?) {
        bundle.putString(
            key,
            Json.encodeToString(value),
        )
    }

    override fun serializeAsValue(value: UserInfo?): String =
        UriUtils.encode(Json.encodeToString(value))
}
