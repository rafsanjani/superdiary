package com.foreverrafs.superdiary.diary.validator

import co.touchlab.kermit.Logger
import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun interface DiaryValidator {
    fun validate(diary: Diary)
}

class DiaryValidatorImpl(
    private val clock: Clock,
) : DiaryValidator {
    override fun validate(diary: Diary) {
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
        Logger.i("Diary validated successfully. $diary")
    }
}
