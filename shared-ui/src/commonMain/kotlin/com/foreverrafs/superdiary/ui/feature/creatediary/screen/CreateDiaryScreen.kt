package com.foreverrafs.superdiary.ui.feature.creatediary.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.foreverrafs.superdiary.diary.Result
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.ui.LocalScreenNavigator
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

object CreateDiaryScreen : Screen {

    @Composable
    override fun Content() {
        val createDiaryScreenModel: CreateDiaryScreenModel = getScreenModel()
        val navigator = LocalScreenNavigator.current

        CreateDiaryScreenContent(
            onNavigateBack = navigator::pop,
        ) { entry ->
            createDiaryScreenModel.saveDiary(
                Diary(
                    entry = entry,
                    date = Clock.System
                        .now(),
                    isFavorite = false,
                ),
            )
        }
    }
}

class CreateDiaryScreenModel(
    private val addDiaryUseCase: AddDiaryUseCase,
) : StateScreenModel<CreateDiaryScreenModel.CreateDiaryScreenState>(CreateDiaryScreenState.Idle) {

    sealed interface CreateDiaryScreenState {
        object Idle : CreateDiaryScreenState
        object Success : CreateDiaryScreenState
        data class Failure(val error: Throwable) : CreateDiaryScreenState
    }

    fun saveDiary(diary: Diary) = screenModelScope.launch {
        when (val result = addDiaryUseCase(diary)) {
            is Result.Success -> mutableState.update {
                CreateDiaryScreenState.Success
            }

            is Result.Failure -> mutableState.update {
                CreateDiaryScreenState.Failure(result.error)
            }
        }
    }
}
