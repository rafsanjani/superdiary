package com.foreverrafs.superdiary.data.grouping

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.utils.groupByDate
import kotlin.random.Random
import kotlin.test.Test
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

class DiaryGroupingTest {
    private val clock = object : Clock {
        override fun now(): Instant = Instant.parse(input = "2023-05-03T01:01:01.049Z")
    }

    @Test
    fun `test diary groupings by day`() {
        val diaries = createDiaries(
            durationSpacing = DateTimeUnit.DAY,
            startDate = Instant.parse(input = "2023-05-01T01:01:01.049Z"),
            count = 3,
        )

        val groups = diaries.groupByDate(clock)

        assertThat(groups.size).isEqualTo(3)
    }

    @Test
    fun `test diary groupings by week`() {
        val diaries = createDiaries(
            durationSpacing = DateTimeUnit.WEEK,
            startDate = Instant.parse(input = "2023-05-01T01:01:01.049Z"),
            count = 3,
        )
        val groups = diaries.groupByDate(clock)

        assertThat(groups.size).isEqualTo(3)
    }

    @Test
    fun `test diary groupings by months`() {
        val diaries = createDiaries(
            durationSpacing = DateTimeUnit.MONTH,
            startDate = Instant.parse(input = "2023-01-01T01:01:01.049Z"),
            count = 3,
        )
        val groups = diaries.groupByDate(clock)

        assertThat(groups.size).isEqualTo(3)
    }

    @Test
    fun `test diary groupings by months and weeks`() {
        val diaries = createDiaries(
            durationSpacing = DateTimeUnit.WEEK,
            startDate = Instant.parse(input = "2023-04-08T01:01:01.049Z"),
            count = 4,
        )
        val groups = diaries.groupByDate(clock)

        assertThat(groups.size).isEqualTo(4)
    }

    @Test
    fun `test diary grouping prioritization`() {
        val diaries = createDiaries(
            durationSpacing = DateTimeUnit.WEEK,
            startDate = Instant.parse(input = "2023-04-08T01:01:01.049Z"),
            count = 4,
        )

        // This grouping should contain days and weeks
        val groups = diaries.groupByDate(clock)
        val priorities = groups.keys.toList()

        var previousPriority = -1
        priorities.forEach { prioritizedDuration ->
            assertThat(prioritizedDuration.priority > previousPriority)
            previousPriority = prioritizedDuration.priority
        }
    }

    private fun createDiaries(
        durationSpacing: DateTimeUnit.DateBased,
        startDate: Instant,
        count: Int,
    ): List<Diary> = (0 until count).map {
        Diary(
            id = Random.nextLong(),
            entry = "Diary Entry #$it",
            date = startDate.plus(
                value = it,
                unit = durationSpacing,
                timeZone = TimeZone.UTC,
            ),
            isFavorite = false,
        )
    }
}
