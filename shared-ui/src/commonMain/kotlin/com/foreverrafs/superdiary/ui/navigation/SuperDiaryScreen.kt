package com.foreverrafs.superdiary.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

interface SuperDiaryScreen {
    @Composable
    fun Content(navController: NavHostController)
}
