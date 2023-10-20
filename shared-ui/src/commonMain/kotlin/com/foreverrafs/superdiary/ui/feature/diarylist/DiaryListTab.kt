package com.foreverrafs.superdiary.ui.feature.diarylist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.ui.feature.creatediary.CreateDiaryScreen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object DiaryListTab : Screen {

    @Composable
    override fun Content() {
        val screenModel: DiaryListScreenModel = getScreenModel()
        val screenState by screenModel.state.collectAsState()

        val navigator = LocalNavigator.currentOrThrow

        DiaryListScreen(
            modifier = Modifier
                .fillMaxSize(),
            state = screenState,
            onAddEntry = {
                navigator.push(
                    CreateDiaryScreen,
                )
            },
        )
    }
}

class DiaryListScreenModel(
    private val getAllDiariesUseCase: GetAllDiariesUseCase,
) : StateScreenModel<DiaryListScreenState>(DiaryListScreenState.Loading) {

    init {
        observeDiaries()
    }

    private fun observeDiaries() = coroutineScope.launch {
        getAllDiariesUseCase().collect { diaries ->
            mutableState.update {
                DiaryListScreenState.Content(diaries)
            }
        }
    }
}
