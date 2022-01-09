package com.foreverrafs.superdiary.framework.presentation.add

import com.foreverrafs.domain.feature_diary.model.Diary


sealed class AddDiaryEvent {
    data class SaveDiary(val diary: Diary) : AddDiaryEvent()
}