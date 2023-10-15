package com.foreverrafs.superdiary.ui.feature.diarylist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.ui.components.DiaryListScreen
import com.foreverrafs.superdiary.ui.feature.creatediary.CreateDiaryScreen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object DiaryListScreen : Screen, KoinComponent {
    private val screenModel: DiaryListPageModel by inject()

    @Composable
    override fun Content() {
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        DiaryListScreen(
            modifier = Modifier
                .fillMaxSize(),
            state = state,
            onAddEntry = {
                navigator.push(
                    CreateDiaryScreen,
                )
            },
        )
    }
}

class DiaryListPageModel(
    private val getAllDiariesUseCase: GetAllDiariesUseCase,
) : StateScreenModel<DiaryListScreenState>(DiaryListScreenState.Loading) {

    init {
        observeDiaries()
    }

    private fun observeDiaries() = coroutineScope.launch {
        getAllDiariesUseCase.diaries.collect { diaries ->
            mutableState.update {
                DiaryListScreenState.Content(diaries)
            }
        }
    }
}
