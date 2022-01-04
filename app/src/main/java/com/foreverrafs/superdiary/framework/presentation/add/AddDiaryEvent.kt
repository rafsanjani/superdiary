package com.foreverrafs.superdiary.framework.presentation.add

import com.foreverrafs.superdiary.business.model.Diary

sealed class AddDiaryEvent {
    data class SaveDiary(val diary: Diary) : AddDiaryEvent()
}