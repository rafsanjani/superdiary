package com.foreverrafs.superdiary.domain.validator

import com.foreverrafs.superdiary.domain.model.Diary
import kotlin.time.Clock
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
    }
}
