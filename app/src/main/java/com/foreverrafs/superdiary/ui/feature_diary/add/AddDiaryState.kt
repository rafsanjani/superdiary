package com.foreverrafs.superdiary.ui.feature_diary.add

import com.foreverrafs.domain.feature_diary.model.Diary

sealed class AddDiaryState {
    data class Error(val error: Throwable) : AddDiaryState()
    data class Success(val diary: Diary) : AddDiaryState()
}