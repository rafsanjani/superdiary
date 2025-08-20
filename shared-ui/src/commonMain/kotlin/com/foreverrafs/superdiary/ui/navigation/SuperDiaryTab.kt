package com.foreverrafs.superdiary.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.VectorPainter

interface SuperDiaryTab {
    val selectedIcon: VectorPainter
        @Composable get

    val options: TabOptions
        @Composable get
}

data class TabOptions(
    val index: UShort,
    val title: String,
    val icon: Painter,
)
