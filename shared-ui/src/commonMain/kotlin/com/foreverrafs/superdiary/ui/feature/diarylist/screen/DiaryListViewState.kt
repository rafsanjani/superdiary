package com.foreverrafs.superdiary.ui.feature.diarylist.screen

import com.foreverrafs.superdiary.domain.model.Diary

sealed interface DiaryListViewState {
    data object Loading : DiaryListViewState
    data class Content(val diaries: List<Diary>, val filtered: Boolean) : DiaryListViewState
    data class Error(val error: Throwable) : DiaryListViewState
}
