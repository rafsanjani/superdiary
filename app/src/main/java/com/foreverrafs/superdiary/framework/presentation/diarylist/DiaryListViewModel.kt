package com.foreverrafs.superdiary.framework.presentation.diarylist

import androidx.lifecycle.viewModelScope
import com.foreverrafs.superdiary.business.Result
import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.business.usecase.common.DeleteDiaryUseCase
import com.foreverrafs.superdiary.business.usecase.diarylist.GetAllDiariesUseCase
import com.foreverrafs.superdiary.framework.presentation.common.BaseViewModel
import com.foreverrafs.superdiary.framework.presentation.diarylist.state.DiaryListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDate
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


    val dateForSelectedDiary: LocalDate
        get() = _selectedDate

    fun setSelectedDateForDiaries(value: LocalDate) {
        _selectedDate = value
    }

    val allDiaries: List<Diary>
        get() = _allDiaries

    init {
        getAllDiaries()
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

    fun deleteDiary(diary: Diary) = viewModelScope.launch(dispatcher) {
        try {
            deleteDiaryUseCase(diary)
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