package com.foreverrafs.superdiary.framework.presentation.diarylist

import com.foreverrafs.domain.feature_diary.model.Diary


sealed class DiaryListState {
    data class Loaded(val list: List<Diary>) : DiaryListState()
    data class Error(val error: Throwable) : DiaryListState()
    object Loading : DiaryListState()
}
