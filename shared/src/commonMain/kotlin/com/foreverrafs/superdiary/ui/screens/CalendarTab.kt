package com.foreverrafs.superdiary.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.foreverrafs.superdiary.ui.components.DiaryListScreen

object CalendarTab : Tab {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel(tag = "diary-list") {
            CalendarTabModel()
        }

        val screenState by screenModel.state.collectAsState()

        LaunchedEffect(Unit) {
        }

        DiaryListScreen(
            modifier = Modifier.fillMaxSize(),
            state = screenState,
        )
    }

    override val key: ScreenKey
        get() = "diary-list-destination"

    override val options: TabOptions
        @Composable
        get() {
            val title = "Home"
            val icon = rememberVectorPainter(Icons.Default.CalendarMonth)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon,
                )
            }
        }
}

private class CalendarTabModel : StateScreenModel<DiaryScreenState>(DiaryScreenState.Loading)
