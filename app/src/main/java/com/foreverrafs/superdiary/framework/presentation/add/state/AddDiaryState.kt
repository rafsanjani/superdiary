package com.foreverrafs.superdiary.framework.presentation.add.state

import com.foreverrafs.superdiary.business.model.Diary

sealed class AddDiaryState {
    data class Saved(val diary: Diary) : AddDiaryState()
    object Saving : AddDiaryState()
    data class Error(val error: Throwable) : AddDiaryState()
}