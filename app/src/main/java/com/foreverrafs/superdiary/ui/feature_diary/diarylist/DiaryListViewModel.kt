package com.foreverrafs.superdiary.ui.feature_diary.diarylist


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.domain.feature_diary.Result
import com.foreverrafs.domain.feature_diary.model.Diary
import com.foreverrafs.domain.feature_diary.usecase.DeleteDiaryUseCase
import com.foreverrafs.domain.feature_diary.usecase.GetAllDiariesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Collections.emptyList
import javax.inject.Inject


@HiltViewModel
class DiaryListViewModel
@Inject
constructor(
    private val fetchAllDiariesUseCase: GetAllDiariesUseCase,
    private val deleteDiaryUseCase: DeleteDiaryUseCase,
    private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _viewState: MutableStateFlow<DiaryListState?> = MutableStateFlow(null)

    val viewState = _viewState.asStateFlow()

    var allDiaries = emptyList<Diary>()

    private var _selectedDate: LocalDate = LocalDate.now()

    private var diaryListUiEvent: MutableStateFlow<DiaryListEvent?> = MutableStateFlow(null)

    val diaryEventDates get() = allDiaries.map { it.date.toLocalDate() }

    init {
        getAllDiaries()

        viewModelScope.launch(dispatcher) {
            diaryListUiEvent.collect { event ->
                event?.let {
                    when (it) {
                        is DiaryListEvent.DeleteDiary -> {

                        }
                        DiaryListEvent.AddDiary -> {

                        }
                    }
                }
            }
        }
    }

    private fun getAllDiaries() = viewModelScope.launch(dispatcher) {
        fetchAllDiariesUseCase()
            .collect { result ->
                when (result) {
                    is Result.Success -> {
                        _viewState.value = DiaryListState.Loaded(result.data)
                        allDiaries = result.data
                    }
                    is Result.Error -> _viewState.value = DiaryListState.Error(result.error)
                }
            }
    }

    fun onEvent(event: DiaryListEvent) {
        diaryListUiEvent.value = event
    }

    fun deleteDiary(diary: Diary) = viewModelScope.launch(dispatcher) {
        deleteDiaryUseCase(diary)
    }

    fun getDiariesForDate(date: LocalDate) {
        val filtered = allDiaries.filter {
            it.date.toLocalDate() == date
        }

        if (filtered.isNotEmpty()) {
            _viewState.value = DiaryListState.Loaded(filtered, true)
        } else {
            _viewState.value = DiaryListState.Loaded(emptyList(), true)
        }
    }
}