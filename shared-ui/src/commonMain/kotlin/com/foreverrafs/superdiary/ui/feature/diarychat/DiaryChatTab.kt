package com.foreverrafs.superdiary.ui.feature.diarychat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.foreverrafs.superdiary.ui.navigation.SuperDiaryTab
import com.foreverrafs.superdiary.ui.navigation.TabOptions
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
object DiaryChatTab : SuperDiaryTab {
    @Composable
    fun Content() {
        val screenModel: DiaryChatViewModel = koinInject()
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
