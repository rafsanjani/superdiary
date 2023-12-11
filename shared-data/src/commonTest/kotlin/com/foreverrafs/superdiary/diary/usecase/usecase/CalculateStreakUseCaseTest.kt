package com.foreverrafs.superdiary.diary.usecase.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.CalculateStreakUseCase
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlin.random.Random
import kotlin.test.Test

class CalculateStreakUseCaseTest {

    private val calculateStreakUseCase: CalculateStreakUseCase = CalculateStreakUseCase()

    @Test
    fun `Adding entries in n consecutive days should give n-1 streaks`() = runTest {
        val diaries = (0..9).map {
            Diary(
                id = Random.nextLong(),
                entry = "New Entry today",
                date = Clock.System.now().minus(it, DateTimeUnit.DAY, TimeZone.UTC),
                isFavorite = false,
            )
        }

        val streaks = calculateStreakUseCase(diaries)

        assertThat(streaks.count).isEqualTo(9)
    }

    @Test
    fun `Adding non-consecutive entries produce 0 streak`() = runTest {
        val diaries = listOf(
            Diary(
                id = Random.nextLong(),
                entry = "New Entry today",
                date = Clock.System.now(),
                isFavorite = false,
            ),
            Diary(
                id = Random.nextLong(),
                entry = "New Entry today",
                date = Clock.System.now().minus(2, DateTimeUnit.DAY, TimeZone.UTC),
                isFavorite = false,
            ),
        )

        val streaks = calculateStreakUseCase(diaries)

        assertThat(streaks.count).isEqualTo(0)
    }
}
