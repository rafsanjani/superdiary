package com.foreverrafs.superdiary.list.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.foreverrafs.superdiary.design.style.animatedComposable
import com.foreverrafs.superdiary.list.presentation.screen.detail.screen.DetailScreen
import com.foreverrafs.superdiary.list.presentation.screen.list.DiaryListScreen

@OptIn(ExperimentalSharedTransitionApi::class)
inline fun <reified T : Any> NavGraphBuilder.diaryListNavigation(
    navController: NavHostController,
    noinline onBackPressed: () -> Unit,
    noinline onAddEntry: () -> Unit,
    noinline onProfileClick: () -> Unit,
) {
    navigation<T>(startDestination = DiaryListRoute.DiaryListScreen) {
        animatedComposable<DiaryListRoute.DiaryListScreen> {
            DiaryListScreen(
                onAddEntry = onAddEntry,
                onDiaryClick = {
                    navController.navigate(DiaryListRoute.DetailScreen(it.toString()))
                },
                onProfileClick = onProfileClick,
                onBackpressed = navController::navigateUp,
            )
        }

        animatedComposable<DiaryListRoute.DetailScreen> { backstackEntry ->
            val diaryId: String = backstackEntry.toRoute<DiaryListRoute.DetailScreen>().diaryId

            DetailScreen(
                diaryId = diaryId,
                onProfileClick = onProfileClick,
                onBackPress = onBackPressed,
            )
        }
    }
}
