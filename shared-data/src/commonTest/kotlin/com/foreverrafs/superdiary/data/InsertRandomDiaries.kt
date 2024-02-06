package com.foreverrafs.superdiary.data

import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.usecase.AddDiaryUseCase
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

fun insertRandomDiaries(dataSource: DataSource, count: Int = 30) {
    runBlocking {
        val relaxedAddDiaryUseCase = AddDiaryUseCase(dataSource) {}

        // March 03, 2023
        var currentDate = Instant.parse(isoString = "2023-03-03T02:35:53.049Z")
        repeat(count) {
            relaxedAddDiaryUseCase(
                Diary(
                    entry = "Diary Entry #$it",
                    date = currentDate,
                    isFavorite = false,
                ),
            )

            currentDate = currentDate.plus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
        }
    }
}
