package com.foreverrafs.superdiary.framework.presentation.add

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.business.usecase.add.AddDiaryUseCase
import com.foreverrafs.superdiary.framework.presentation.add.state.AddDiaryState
import com.foreverrafs.superdiary.framework.presentation.common.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

// TODO: 28/12/20 Save user's text across process death
// TODO: 28/12/20 Allow user to save entry as draft when navigating away from a non-empty screen
class AddDiaryViewModel @ViewModelInject constructor(
    private val addDiary: AddDiaryUseCase,
    private val dispatcher: CoroutineDispatcher
) : BaseViewModel<AddDiaryState>() {

    fun saveDiary(diary: Diary) = viewModelScope.launch(dispatcher) {
        try {
            addDiary(diary)
            setViewState(AddDiaryState.Saved(diary))
        } catch (throwable: Throwable) {
            setViewState(AddDiaryState.Error(throwable))
        }
    }
}