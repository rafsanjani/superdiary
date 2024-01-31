package com.foreverrafs.superdiary.ui.feature.details

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.DeleteDiaryUseCase
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val deleteDiaryUseCase: DeleteDiaryUseCase,
) :
    StateScreenModel<DetailsViewModel.DetailsScreenState>(DetailsScreenState.Idle) {
    sealed interface DetailsScreenState {
        data object Idle : DetailsScreenState
        data object DiaryDeleted : DetailsScreenState
    }

    fun deleteDiary(diary: Diary) = screenModelScope.launch {
        if (deleteDiaryUseCase(listOf(diary)) != 0) {
            mutableState.update {
                DetailsScreenState.DiaryDeleted
            }
        }
    }
}
