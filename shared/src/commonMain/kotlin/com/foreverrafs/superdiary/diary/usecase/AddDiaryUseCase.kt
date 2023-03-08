package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.Result
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class AddDiaryUseCase(private val dataSource: DataSource) {
    suspend operator fun invoke(diary: Diary): Result {
        return try {
            checkPreconditions(diary)

            dataSource.add(diary)
            Result.Success(data = listOf(diary))
        } catch (ex: IllegalArgumentException) {
            Result.Failure(ex)
        }
    }

    private fun checkPreconditions(diary: Diary) {
        val diaryDate =
            Instant.parse(diary.date).toLocalDateTime(TimeZone.currentSystemDefault()).date
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        require(diaryDate == today) {
            val placeHolder = if (diaryDate > today) {
                "future"
            } else {
                "past"
            }

            "Saving a diary entry in the $placeHolder is an error. [diary date = $diaryDate, today = $today]"
        }
    }
}
