package com.foreverrafs.superdiary.framework.presentation.diarylist


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.superdiary.business.Result
import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.business.usecase.DeleteDiaryUseCase
import com.foreverrafs.superdiary.business.usecase.GetAllDiariesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
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
) : ViewModel() {

    private val _viewState: MutableStateFlow<DiaryListState?> = MutableStateFlow(null)

    val viewState = _viewState.asStateFlow()

    private var _allDiaries = emptyList<Diary>()

    private var _selectedDate: LocalDate = LocalDate.now()

    private var diaryListUiEvent: MutableStateFlow<DiaryListEvent?> = MutableStateFlow(null)


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
                        DiaryListEvent.AddDiary -> {

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
                    _viewState.value = DiaryListState.Error(cause)
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
        diaryListUiEvent.value = event
    }

    fun deleteDiary(diary: Diary) = viewModelScope.launch(dispatcher) {
        deleteDiaryUseCase(diary)
    }

    fun getDiariesForDate(date: LocalDate) {
        val filtered = _allDiaries.filter {
            it.date.toLocalDate() == date
        }

        if (filtered.isNotEmpty()) {
            _viewState.value = DiaryListState.Loaded(filtered)
        } else {
            _viewState.value = DiaryListState.Loaded(emptyList())
        }
    }
}