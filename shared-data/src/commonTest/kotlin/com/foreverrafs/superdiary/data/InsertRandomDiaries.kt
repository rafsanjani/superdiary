package com.foreverrafs.superdiary.data

import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.usecase.AddDiaryUseCase
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

suspend fun insertRandomDiaries(dataSource: DataSource, count: Int = 30) {
    val relaxedAddDiaryUseCase = AddDiaryUseCase(dataSource, TestAppDispatchers) {}

    // March 03, 2023
    var currentDate = Instant.parse(input = "2023-03-03T02:35:53.049Z")
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
