package com.foreverrafs.superdiary.list.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.foreverrafs.superdiary.design.style.animatedComposable
import com.foreverrafs.superdiary.list.presentation.screen.DiaryListScreen

@OptIn(ExperimentalSharedTransitionApi::class)
inline fun <reified T : Any> NavGraphBuilder.diaryListNavigation(
    navController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    noinline onAddEntry: () -> Unit,
    noinline onDiaryClick: (id: Long) -> Unit,
    noinline onProfileClick: () -> Unit,
) {
    navigation<T>(startDestination = DiaryListRoute.DiaryListScreen) {
        animatedComposable<DiaryListRoute.DiaryListScreen> {
            DiaryListScreen(
                navController = navController,
                avatarUrl = null,
                onAddEntry = onAddEntry,
                onDiaryClick = onDiaryClick,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = this@animatedComposable,
                onProfileClick = onProfileClick,
            )
        }
    }
}
