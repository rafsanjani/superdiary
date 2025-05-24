package com.foreverrafs.superdiary.list.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.foreverrafs.superdiary.design.style.animatedComposable
import com.foreverrafs.superdiary.list.presentation.screen.detail.screen.DetailScreen
import com.foreverrafs.superdiary.list.presentation.screen.list.DiaryListScreen

@OptIn(ExperimentalSharedTransitionApi::class)
inline fun <reified T : Any> NavGraphBuilder.diaryListNavigation(
    navController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    noinline onAddEntry: () -> Unit,
    noinline onProfileClick: () -> Unit,
) {
    navigation<T>(startDestination = DiaryListRoute.DiaryListScreen) {
        animatedComposable<DiaryListRoute.DiaryListScreen> {
            DiaryListScreen(
                navController = navController,
                avatarUrl = null,
                onAddEntry = onAddEntry,
                onDiaryClick = {
                    navController.navigate(DiaryListRoute.DetailScreen(it.toString()))
                },
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = this@animatedComposable,
                onProfileClick = onProfileClick,
            )
        }

        animatedComposable<DiaryListRoute.DetailScreen>(
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = DiaryListRoute.DetailScreen.DEEPLINK_URI_PATTERN
                },
            ),
        ) { backstackEntry ->
            val diaryId: String = backstackEntry.toRoute<DiaryListRoute.DetailScreen>().diaryId

            DetailScreen(
                diaryId = diaryId,
                navController = navController,
                onProfileClick = onProfileClick,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScpe = this@animatedComposable,
            )
        }
    }
}
