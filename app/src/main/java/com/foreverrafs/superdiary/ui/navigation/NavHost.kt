package com.foreverrafs.superdiary.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ViewDay
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
    val scaffoldState = rememberScaffoldState()

    Scaffold {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = "${BottomNavDestination.DiaryList.route}?showSnackBar={showSnackBar}?snackbarMessage={snackbarMessage}"
        ) {
            composable(
                route = "${BottomNavDestination.DiaryList.route}?showSnackBar={showSnackBar}?snackbarMessage={snackbarMessage}",
                arguments = listOf(
                    navArgument("showSnackBar") {
                        defaultValue = false
                        type = NavType.BoolType
                    },
                    navArgument("snackbarMessage") {
                        defaultValue = ""
                        type = NavType.StringType
                    }
                )
            ) {

                val showSnackBar = it.arguments?.getBoolean("showSnackBar") ?: false
                val snackbarMessage = it.arguments?.getString("snackbarMessage") ?: ""

                it.arguments?.clear()

                DiaryListScreen(
                    navController = navController,
                    showSnackBar = showSnackBar,
                    snackBarMessage = snackbarMessage
                )
            }

            composable(
                route = BottomNavDestination.AddDiary.route,
            ) {
                AddDiaryScreen(
                    scaffoldState = scaffoldState,
                    onDiarySaved = {
                        val snackbarMessage = "Diary Saved Successfully"

                        navController.navigate(
                            route = BottomNavDestination.DiaryList.route + "?showSnackBar=true?snackbarMessage=$snackbarMessage"
                        )
                    },
                )
            }
        }
    }
}

sealed class BottomNavDestination(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    object DiaryList :
        BottomNavDestination(title = "List", icon = Icons.Default.List, route = "diary_list")

    object AddDiary :
        BottomNavDestination(title = "Add Diary", icon = Icons.Default.Add, route = "add_diary")

    object Calendar :
        BottomNavDestination(
            title = "Calendar",
            icon = Icons.Default.ViewDay,
            route = "calendary"
        )
}
        