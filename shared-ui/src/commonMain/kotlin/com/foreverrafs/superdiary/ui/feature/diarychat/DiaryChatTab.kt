package com.foreverrafs.superdiary.ui.feature.diarychat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.foreverrafs.superdiary.ui.SuperDiaryTab

object DiaryChatTab : SuperDiaryTab {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<DiaryChatViewModel>()
        val screenState by screenModel.state.collectAsState()

        DiaryChatScreenContent(
            screenState = screenState,
            onQueryDiaries = screenModel::queryDiaries,
        )
    }

    override val selectedIcon: VectorPainter
        @Composable
        get() = rememberVectorPainter(Icons.AutoMirrored.Filled.Chat)
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 4u,
            title = "Diary AI",
            icon = rememberVectorPainter(Icons.AutoMirrored.Outlined.Chat),
        )
}
