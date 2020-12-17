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

                setViewState(DiaryListState.DiaryList(sortedDiaries))
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

    fun filterDiariesByDate(list: List<Diary>, date: LocalDate): List<Diary> {
        return list.filter {
            it.date.toLocalDate() == date
        }
    }
}