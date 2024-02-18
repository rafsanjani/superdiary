package com.foreverrafs.superdiary.ui.feature.details

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.usecase.DeleteDiaryUseCase
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
        when (val result = deleteDiaryUseCase(listOf(diary))) {
            is Result.Failure -> {
                // TODO: Handle unhappy path gracefully when deleting diary from details
            }

            is Result.Success -> {
                val deletedItems = result.data
                if (deletedItems != 0) {
                    mutableState.update {
                        DetailsScreenState.DiaryDeleted
                    }
                }
            }
        }
    }
}
