package com.foreverrafs.superdiary.ui.feature_diary.diarylist

import com.foreverrafs.domain.feature_diary.model.Diary


sealed class DiaryListState {
    data class Loaded(val list: List<Diary>) : DiaryListState()
    data class Error(val error: Throwable) : DiaryListState()
    object Loading : DiaryListState()
}
