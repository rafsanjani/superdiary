package com.foreverrafs.superdiary.diary.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.utils.groupByDate
import kotlin.random.Random
import kotlin.test.Test
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

class DiaryGroupingTest {

    @Test
    fun `test diary groupings by day`() {
        val diaries = createDiaries(
            durationSpacing = DateTimeUnit.DAY,
            startDate = Instant.parse(isoString = "2023-05-01T01:01:01.049Z"),
            count = 3
        )
        val groups = diaries.groupByDate()

        assertThat(groups.size).isEqualTo(3)
    }

    @Test
    fun `test diary groupings by week`() {
        val clock = object : Clock {
            override fun now(): Instant = Instant.parse(isoString = "2023-01-28T01:01:01.049Z")
        }

        val diaries = createDiaries(
            durationSpacing = DateTimeUnit.WEEK,
            startDate = Instant.parse(isoString = "2023-01-01T01:01:01.049Z"),
            count = 3
        )
        val groups = diaries.groupByDate(clock)

        assertThat(groups.size).isEqualTo(3)
    }

    @Test
    fun `test diary groupings by months`() {
        val clock = object : Clock {
            override fun now(): Instant = Instant.parse(isoString = "2023-04-01T01:01:01.049Z")
        }

        val diaries = createDiaries(
            durationSpacing = DateTimeUnit.MONTH,
            startDate = Instant.parse(isoString = "2023-01-01T01:01:01.049Z"),
            count = 3
        )
        val groups = diaries.groupByDate(clock)

        assertThat(groups.size).isEqualTo(3)
    }

    @Test
    fun `test diary groupings by months and weeks`() {
        val clock = object : Clock {
            override fun now(): Instant = Instant.parse(isoString = "2023-05-08T01:01:01.049Z")
        }

        val diaries = createDiaries(
            durationSpacing = DateTimeUnit.WEEK,
            startDate = Instant.parse(isoString = "2023-04-08T01:01:01.049Z"),
            count = 4
        )
        val groups = diaries.groupByDate(clock)

        assertThat(groups.size).isEqualTo(4)
    }

    private fun createDiaries(
        durationSpacing: DateTimeUnit,
        startDate: Instant,
        count: Int,
    ): List<Diary> {
        return (0 until count).map {
            Diary(
                id = Random.nextLong(),
                entry = "Diary Entry #$it",
                date = startDate.plus(
                    it.toLong(),
                    durationSpacing,
                    TimeZone.currentSystemDefault()
                ).toString(),
            )
        }
    }
}
