package com.foreverrafs.superdiary.ui.feature.diaryai

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

object DiaryAiScreen : Tab {
    @Composable
    override fun Content() {
        ChatScreenContent()
    }

    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 4u,
            title = "Diary AI",
            icon = rememberVectorPainter(Icons.Default.ChatBubble),
        )
}
