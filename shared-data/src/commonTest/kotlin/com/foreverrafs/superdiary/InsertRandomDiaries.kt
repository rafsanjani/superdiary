package com.foreverrafs.superdiary

import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

@OptIn(ExperimentalTime::class)
suspend fun insertRandomDiaries(dataSource: DataSource, count: Int = 30) {
    // March 03, 2023
    var currentDate = Instant.parse(input = "2023-03-03T02:35:53.049Z")

    repeat(count) {
        dataSource.save(
            Diary(
                entry = "Diary Entry #$it",
                date = currentDate,
                isFavorite = false,
            ),
        )

        currentDate = currentDate.plus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
    }
}
