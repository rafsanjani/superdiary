package com.foreverrafs.superdiary.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
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
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.ui.components.DiaryListScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

object DiaryListTab : Tab {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel(tag = "diary-list") {
            DiaryListTabModel()
        }

        val screenState by screenModel.state.collectAsState()

        LaunchedEffect(Unit) {
            screenModel.observeDiaries()
        }

        ScreenContainer {
            DiaryListScreen(
                modifier = Modifier
                    .fillMaxSize(),
                state = screenState,
            )
        }
    }

    override val key: ScreenKey
        get() = "diary-list-destination"

    override val options: TabOptions
        @Composable
        get() {
            val title = "Home"
            val icon = rememberVectorPainter(Icons.Default.List)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon,
                )
            }
        }
}

private class DiaryListTabModel : StateScreenModel<DiaryScreenState>(DiaryScreenState.Loading) {
    suspend fun observeDiaries() {
        delay(1500)
        mutableState.update {
            DiaryScreenState.Content(
                diaries = (0..10).map {
                    Diary(
                        id = Random.nextLong(),
                        entry = "Hello $it",
                        date = Clock.System.now()
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                            .date
                            .toString(),
                    )
                },
            )
        }
    }
}
