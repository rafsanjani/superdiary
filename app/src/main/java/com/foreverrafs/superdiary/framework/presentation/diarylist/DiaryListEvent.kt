package com.foreverrafs.superdiary.framework.presentation.diarylist

import com.foreverrafs.superdiary.business.model.Diary

sealed class DiaryListEvent {
    data class DeleteDiary(val diary: Diary) : DiaryListEvent()
    object AddDiary : DiaryListEvent()
}