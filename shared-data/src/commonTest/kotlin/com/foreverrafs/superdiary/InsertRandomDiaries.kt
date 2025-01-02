package com.foreverrafs.superdiary

import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.domain.usecase.AddDiaryUseCase
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
