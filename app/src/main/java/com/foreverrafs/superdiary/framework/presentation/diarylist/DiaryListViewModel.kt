package com.foreverrafs.superdiary.framework.presentation.diarylist

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.business.usecase.diarylist.DiaryListInteractor
import com.foreverrafs.superdiary.framework.presentation.common.BaseViewModel
import com.foreverrafs.superdiary.framework.presentation.diarylist.state.DiaryListState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDate


class DiaryListViewModel @ViewModelInject constructor(
    private val listInteractor: DiaryListInteractor,
    private val dispatcher: CoroutineDispatcher
) : BaseViewModel<DiaryListState>() {

    private var _allDiaries = listOf<Diary>()
    private var _selectedDate: LocalDate = LocalDate.now()


    val selectedDate: LocalDate = _selectedDate
    fun setSelectedDate(value: LocalDate) {
        _selectedDate = value
    }

    val allDiaries: List<Diary>
        get() = _allDiaries

    init {
        getAllDiaries()
    }

    private fun getAllDiaries() = viewModelScope.launch(dispatcher) {
        listInteractor.fetchAllDiaries()
            .catch {
                it.cause?.let { cause ->
                    setViewState(DiaryListState.Error(cause))
                }
            }
            .collect {
                val sortedDiaries = it.sortedByDescending { diaryItem ->
                    diaryItem.date
                }

                _allDiaries = sortedDiaries

                getDiariesForDate(_selectedDate)
            }
    }

    fun deleteDiary(diary: Diary) = viewModelScope.launch(dispatcher) {
        try {
            listInteractor.deleteDiary(diary)
            setViewState(DiaryListState.Deleted(diary))
        } catch (exception: Throwable) {
            setViewState(DiaryListState.Error(exception))
        }
    }

    fun getDiariesForDate(date: LocalDate) {
        val filtered = _allDiaries.filter {
            it.date.toLocalDate() == date
        }

        if (filtered.isNotEmpty()) {
            setViewState(DiaryListState.DiaryList(filtered))
        } else {
            setViewState(DiaryListState.Empty)
        }
    }
}