package com.foreverrafs.superdiary.ui.screens

import com.foreverrafs.superdiary.diary.model.Diary

sealed interface DiaryScreenState {
    data object Loading : DiaryScreenState
    data class Content(val diaries: List<Diary>) : DiaryScreenState
    data class Error(val error: Throwable) : DiaryScreenState
}
