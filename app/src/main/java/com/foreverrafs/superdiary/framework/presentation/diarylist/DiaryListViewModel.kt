package com.foreverrafs.superdiary.framework.presentation.diarylist


import androidx.lifecycle.viewModelScope
import com.foreverrafs.superdiary.business.Result
import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.business.usecase.common.DeleteDiaryUseCase
import com.foreverrafs.superdiary.business.usecase.diarylist.GetAllDiariesUseCase
import com.foreverrafs.superdiary.framework.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
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
) : BaseViewModel<DiaryListState>() {
    private var _allDiaries = emptyList<Diary>()

    private var _selectedDate: LocalDate = LocalDate.now()

    private var _diaryListUiEvent: MutableStateFlow<DiaryListEvent?> = MutableStateFlow(null)

    private val diaryListUiEvent: StateFlow<DiaryListEvent?> = _diaryListUiEvent.asStateFlow()

    val allDiaries: List<Diary>
        get() = _allDiaries

    val diaryEventDates get() = allDiaries.map { it.date.toLocalDate() }

    init {
        getAllDiaries()

        viewModelScope.launch(dispatcher) {
            diaryListUiEvent.collect { event ->
                event?.let {
                    when (it) {
                        is DiaryListEvent.DeleteDiary -> {

                        }
                        DiaryListEvent.AddDiary ->{

                        }
                    }
                }
            }
        }
    }

    private fun getAllDiaries() = viewModelScope.launch(dispatcher) {
        fetchAllDiariesUseCase()
            .catch {
                it.cause?.let { cause ->
                    setViewState(DiaryListState.Error(cause))
                }
            }
            .collect {
                when (it) {
                    is Result.Error -> {
                        //render error state here
                    }
                    is Result.Success -> {
                        val sortedDiaries = it.data.sortedByDescending { diaryItem ->
                            diaryItem.date
                        }

                        _allDiaries = sortedDiaries
                        getDiariesForDate(_selectedDate)
                    }
                }
            }
    }

    fun onEvent(event: DiaryListEvent) {
        _diaryListUiEvent.value = event
    }

    fun deleteDiary(diary: Diary) = viewModelScope.launch(dispatcher) {
        deleteDiaryUseCase(diary)
    }

    fun getDiariesForDate(date: LocalDate) {
        val filtered = _allDiaries.filter {
            it.date.toLocalDate() == date
        }

        if (filtered.isNotEmpty()) {
            setViewState(DiaryListState.Loaded(filtered))
        } else {
            setViewState(DiaryListState.Loaded(emptyList()))
        }
    }
}