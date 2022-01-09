package com.example.composesamples.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.foreverrafs.superdiary.ui.feature_diary.add.AddDiaryScreen
import com.foreverrafs.superdiary.ui.feature_diary.diarylist.DiaryListScreen


@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun SuperDiaryNavHost(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = SuperDiaryNavDestination.DiaryList()
    ) {
        composable(
            route = SuperDiaryNavDestination.DiaryList()
        ) {
            DiaryListScreen(navController = navController)
        }
        composable(
            route = SuperDiaryNavDestination.AddDiary()
        ) {
            AddDiaryScreen(navController = navController)
        }
    }
}

sealed class SuperDiaryNavDestination(
    val route: String
) {
    operator fun invoke() = route

    object DiaryList : SuperDiaryNavDestination(route = "diary_list")

    object AddDiary : SuperDiaryNavDestination(route = "add_diary")
}
        