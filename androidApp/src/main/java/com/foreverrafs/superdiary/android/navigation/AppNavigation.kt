package com.foreverrafs.superdiary.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.foreverrafs.superdiary.android.di.AppScreens
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    appScreens: AppScreens
) {
    AnimatedNavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Routes.DiaryTimelineScreen
    ) {
        composable(
            route = Routes.DiaryTimelineScreen,
        ) {
            appScreens.diaryTimelineScreen()
        }

        composable(route = Routes.CalendarScreen) {
            appScreens.calendarScreen()
        }

        composable(route = Routes.CreateDiaryScreen) {
            appScreens.createDiaryScreen()
        }
    }
}

object Routes {
    const val CalendarScreen = "calendar-screen"
    const val CreateDiaryScreen = "create-diary"
    const val DiaryTimelineScreen = "diary-timeline"
}
