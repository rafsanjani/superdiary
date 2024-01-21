package com.foreverrafs.superdiary.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.VectorPainter
import cafe.adriel.voyager.navigator.tab.Tab

interface SuperDiaryTab : Tab {
    val selectedIcon: VectorPainter
        @Composable get
}
