package com.foreverrafs.superdiary.framework.presentation.add

import com.foreverrafs.superdiary.business.model.Diary

sealed class AddDiaryState {
    data class Error(val error: Throwable) : AddDiaryState()
    data class Success(val diary: Diary) : AddDiaryState()
}