package com.foreverrafs.superdiary.diary.utils

import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DiaryValidator(
    private val clock: Clock,
) {
    fun validate(diary: Diary) {
        val diaryDate = diary.date.toLocalDateTime(TimeZone.UTC).date
        val today = clock.now().toLocalDateTime(TimeZone.UTC).date

        require(diaryDate == today) {
            val placeHolder = if (diaryDate > today) {
                "future"
            } else {
                "past"
            }

            "Saving a diary entry in the $placeHolder is an error.," +
                "[diary date = $diaryDate, today = $today]"
        }
    }
}
