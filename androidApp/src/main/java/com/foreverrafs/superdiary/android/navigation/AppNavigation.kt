package com.foreverrafs.superdiary.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost

@Composable
fun AppNavigation(modifier: Modifier = Modifier, navController: NavHostController) {
    AnimatedNavHost(modifier = modifier, navController = navController, startDestination = "") {

    }
}