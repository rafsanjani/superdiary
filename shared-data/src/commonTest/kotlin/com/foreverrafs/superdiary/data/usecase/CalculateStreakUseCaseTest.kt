package com.foreverrafs.superdiary.data.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.data.model.Diary
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
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
    fun `Adding consecutive days after a streak break should restart it`() = runTest {
        // Add consecutive entries
        val diaries = (0..9).map {
            Diary(
                id = Random.nextLong(),
                entry = "New Entry today",
                date = Clock.System.now().plus(it, DateTimeUnit.DAY, TimeZone.UTC),
                isFavorite = false,
            )
        }.toMutableList()

        // Break it by adding consecutive diaries a few days later
        val newDiaries = (0..4).map {
            Diary(
                id = Random.nextLong(),
                entry = "New Entry today",
                date = Clock.System.now().plus(it + 15, DateTimeUnit.DAY, TimeZone.UTC),
                isFavorite = false,
            )
        }

        diaries.addAll(newDiaries)

        val streaks = calculateStreakUseCase(diaries)

        assertThat(streaks.count).isEqualTo(4)
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
