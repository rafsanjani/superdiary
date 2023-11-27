package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlin.random.Random

fun insertRandomDiaries(dataSource: DataSource) {
    runBlocking {
        val relaxedAddDiaryUseCase = RelaxedAddDiaryUseCase(dataSource)

        // March 03, 2023
        var currentDate = Instant.parse(isoString = "2023-03-03T02:35:53.049Z")
        repeat(30) {
            relaxedAddDiaryUseCase(
                Diary(
                    Random.nextLong(),
                    entry = "Diary Entry #$it",
                    date = currentDate,
                    isFavorite = false,
                ),
            )

            currentDate = currentDate.plus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
        }
    }
}
