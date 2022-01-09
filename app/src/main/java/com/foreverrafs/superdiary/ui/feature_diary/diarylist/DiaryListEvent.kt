package com.foreverrafs.superdiary.ui.feature_diary.diarylist

import com.foreverrafs.domain.feature_diary.model.Diary

sealed class DiaryListEvent {
    data class DeleteDiary(val diary: Diary) : DiaryListEvent()
    object AddDiary : DiaryListEvent()
}