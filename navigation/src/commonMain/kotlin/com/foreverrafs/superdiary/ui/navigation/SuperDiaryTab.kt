package com.foreverrafs.superdiary.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey

interface SuperDiaryTab : NavKey {
    val selectedIcon: ImageVector
    val title: String
    val icon: ImageVector
}
