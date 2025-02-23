package com.foreverrafs.superdiary.ui.feature.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.usecase.DeleteDiaryUseCase
import com.foreverrafs.superdiary.domain.usecase.GetDiaryByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface DeleteDiaryState {
    data class Success(val count: Int) : DeleteDiaryState
    data object Failure : DeleteDiaryState
}

sealed interface DetailsViewState {
    data class DiarySelected(val diary: Diary) : DetailsViewState
}

class DetailsViewModel(
    private val deleteDiaryUseCase: DeleteDiaryUseCase,
    private val getDiaryByIdUseCase: GetDiaryByIdUseCase,
    private val logger: AggregateLogger,
) : ViewModel() {

    private val _deleteDiaryState = MutableStateFlow<DeleteDiaryState?>(null)
    val deleteDiaryState: StateFlow<DeleteDiaryState?> = _deleteDiaryState.asStateFlow()

    private val _detailsViewState = MutableStateFlow<DetailsViewState?>(null)
    val detailsViewState: StateFlow<DetailsViewState?> = _detailsViewState.asStateFlow()

    fun deleteDiary(diary: Diary) = viewModelScope.launch {
        when (val result = deleteDiaryUseCase(listOf(diary))) {
            is Result.Failure -> {
                _deleteDiaryState.update {
                    DeleteDiaryState.Failure
                }
            }

            is Result.Success -> {
                val deletedItems = result.data

                if (deletedItems != 0) {
                    _deleteDiaryState.update {
                        DeleteDiaryState.Success(deletedItems)
                    }
                } else {
                    _deleteDiaryState.update {
                        DeleteDiaryState.Failure
                    }
                }
            }
        }
    }

    fun initForDiary(diaryId: Long) = viewModelScope.launch {
        logger.d(TAG) {
            "Selecting diary with id $diaryId"
        }

        val diary = getDiaryByIdUseCase(diaryId)

        _detailsViewState.update {
            if (diary != null) {
                DetailsViewState.DiarySelected(diary)
            } else {
                it
            }
        }
    }

    companion object {
        private const val TAG = "DetailsViewModel"
    }
}
