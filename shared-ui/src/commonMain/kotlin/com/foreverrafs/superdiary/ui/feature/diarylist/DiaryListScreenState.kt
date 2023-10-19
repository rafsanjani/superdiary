package com.foreverrafs.superdiary.ui.feature.diarylist

import com.foreverrafs.superdiary.diary.model.Diary

sealed interface DiaryListScreenState {
    data object Loading : DiaryListScreenState
    data class Content(val diaries: List<Diary>) : DiaryListScreenState
    data class Error(val error: Throwable) : DiaryListScreenState
}
