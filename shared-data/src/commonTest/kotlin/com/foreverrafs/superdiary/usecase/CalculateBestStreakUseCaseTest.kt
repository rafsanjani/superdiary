package com.foreverrafs.superdiary.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.usecase.CalculateBestStreakUseCase
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

@OptIn(ExperimentalCoroutinesApi::class)
class CalculateBestStreakUseCaseTest {

    private val calculateBestStreakUseCase: CalculateBestStreakUseCase = CalculateBestStreakUseCase(
        TestAppDispatchers,
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(TestAppDispatchers.main)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

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
