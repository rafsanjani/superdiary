package com.foreverrafs.superdiary.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.foreverrafs.superdiary.ui.feature.creatediary.screen.CreateDiaryScreen
import com.foreverrafs.superdiary.ui.feature.details.DetailScreen
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListScreen
import com.foreverrafs.superdiary.ui.home.BottomNavigationScreen
import com.foreverrafs.superdiary.ui.style.SuperdiaryTheme

/**
 * Entry point into the whole app. In an ideal world we'll only just render
 * this composable on individual platforms but the world isn't ideal, huh!
 */

@Composable
fun App(modifier: Modifier = Modifier) {
    SuperdiaryTheme {
        val navController = rememberNavController()

        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = BottomNavigationScreen,
        ) {
            composable<BottomNavigationScreen>(
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(400),
                    )
                },
                exitTransition = {
                    slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.End)
                },
                popExitTransition = {
                    slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Start)
                },
                popEnterTransition = {
                    slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.End)
                },
            ) {
                BottomNavigationScreen.Content(navController)
            }

            composable<CreateDiaryScreen> {
                CreateDiaryScreen.Content(navController)
            }

            composable<DiaryListScreen>(
                enterTransition = {
                    slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Start)
                },
                exitTransition = {
                    slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.End)
                },
                popExitTransition = {
                    slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Start)
                },
            ) {
                DiaryListScreen.Content(navController)
            }

            composable<DetailScreen>(
                enterTransition = {
                    slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Start)
                },
                exitTransition = {
                    slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.End)
                },
                popExitTransition = {
                    slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Start)
                },
            ) { backstackEntry ->
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
}
