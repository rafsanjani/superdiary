package com.foreverrafs.superdiary.diary.usecase.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.CalculateBestStreakUseCase
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlin.random.Random
import kotlin.test.Test

class CalculateBestStreakUseCaseTest {

    private val calculateBestStreakUseCase: CalculateBestStreakUseCase = CalculateBestStreakUseCase()

    @Test
    fun `Best streak returns the highest streak count`() = runTest {
        // Create a streak
        val diaries = (0..9).map {
            Diary(
                id = Random.nextLong(),
                entry = "New Entry today",
                date = Clock.System.now().plus(it, DateTimeUnit.DAY, TimeZone.UTC),
                isFavorite = false,
            )
        }.toMutableList()

        // Break it and create another streak a few days later
        val newDiaries = (0..4).map {
            Diary(
                id = Random.nextLong(),
                entry = "New Entry today",
                date = Clock.System.now().plus(it + 15, DateTimeUnit.DAY, TimeZone.UTC),
                isFavorite = false,
            )
        }

        diaries.addAll(newDiaries)

        // Best streak should return the streak with the highest count
        val streaks = calculateBestStreakUseCase(diaries)

        assertThat(streaks.count).isEqualTo(9)
    }
}
