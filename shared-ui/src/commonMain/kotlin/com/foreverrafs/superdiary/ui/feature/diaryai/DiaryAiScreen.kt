package com.foreverrafs.superdiary.ui.feature.diaryai

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.foreverrafs.superdiary.ui.SuperDiaryScreen

object DiaryAiScreen : SuperDiaryScreen() {
    @Composable
    override fun Content() {
        ChatScreenContent()
    }

    override val selectedIcon: VectorPainter
        @Composable
        get() = rememberVectorPainter(Icons.Filled.Chat)
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 4u,
            title = "Diary AI",
            icon = rememberVectorPainter(Icons.Outlined.Chat),
        )
}
