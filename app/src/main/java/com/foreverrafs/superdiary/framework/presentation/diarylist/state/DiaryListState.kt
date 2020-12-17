package com.foreverrafs.superdiary.framework.presentation.diarylist.state

import com.foreverrafs.superdiary.business.model.Diary

sealed class DiaryListState {
    data class DiaryList(val list: List<Diary>) : DiaryListState()
    data class Error(val error: Throwable) : DiaryListState()
    object Loading : DiaryListState()
    data class Deleted(val diary: Diary): DiaryListState()
}
