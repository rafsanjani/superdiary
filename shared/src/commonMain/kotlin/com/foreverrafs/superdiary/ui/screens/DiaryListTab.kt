package com.foreverrafs.superdiary.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.ui.components.DiaryListScreen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object DiaryListTab : Tab, KoinComponent {

    private val screenModel: DiaryListTabModel by inject()

    @Composable
    override fun Content() {
        val state by screenModel.state.collectAsState()

        DiaryListScreen(
            modifier = Modifier
                .fillMaxSize(),
            state = state,
        )
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

class DiaryListTabModel(
    private val getAllDiariesUseCase: GetAllDiariesUseCase,
) : StateScreenModel<DiaryScreenState>(DiaryScreenState.Loading) {

    init {
        observeDiaries()
    }

    private fun observeDiaries() = coroutineScope.launch {
        getAllDiariesUseCase.diaries.collect { diaries ->
            mutableState.update {
                DiaryScreenState.Content(diaries)
            }
        }
    }
}
