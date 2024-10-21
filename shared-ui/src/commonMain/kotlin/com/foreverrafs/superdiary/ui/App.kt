package com.foreverrafs.superdiary.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.superdiary.core.utils.localActivityWrapper
import com.foreverrafs.superdiary.ui.style.SuperdiaryTheme
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

/**
 * Entry point into the whole app. In an ideal world we'll only just render
 * this composable on individual platforms but the world isn't ideal, huh!
 */

@Composable
fun App(modifier: Modifier = Modifier) {
    SuperdiaryTheme {
        val navController = rememberNavController()
        val context = localActivityWrapper()

        val coroutine = rememberCoroutineScope()
        val authApi: AuthApi = koinInject {
            parametersOf(context)
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Button(onClick = {
                coroutine.launch {
                    authApi.signInWithGoogle()
                }
            }) {
                Text("Click me fool!")
            }
        }

//        NavHost(
//            modifier = modifier,
//            navController = navController,
//            startDestination = BottomNavigationScreen,
//        ) {
//            animatedComposable<BottomNavigationScreen> {
//                BottomNavigationScreen.Content(navController)
//            }
//
//            composable<CreateDiaryScreen> {
//                CreateDiaryScreen.Content(navController)
//            }
//
//            animatedComposable<DiaryListScreen> {
//                DiaryListScreen.Content(navController)
//            }
//
//            animatedComposable<DetailScreen> { backstackEntry ->
//                val diaryId: String? = backstackEntry.arguments?.getString("diaryId")
//
//                diaryId?.let {
//                    DetailScreen.Content(
//                        diaryId = diaryId,
//                        navController = navController,
//                    )
//                }
//            }
//        }
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
        300,
        easing = LinearEasing,
    ),
)

private fun exitTransition() = fadeOut(
    animationSpec = tween(
        300,
        easing = LinearEasing,
    ),
)
