package com.foreverrafs.superdiary.android.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.ui.graphics.vector.ImageVector

@Suppress("unused")
enum class BottomBarDestination(
    val icon: ImageVector,
    val label: String,
    val route: String,
) {
    Timeline(Icons.Default.Timeline, "Timeline", "timeline"),
    Calendar(Icons.Default.CalendarMonth, "Timeline", "timeline"),
}
