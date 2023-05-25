package com.foreverrafs.superdiary.diary.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.utils.groupByDate
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlin.random.Random
import kotlin.test.Test

class DiaryGroupingTest {
    private val clock = object : Clock {
        override fun now(): Instant = Instant.parse(isoString = "2023-05-03T01:01:01.049Z")
    }

    @Test
    fun `test diary groupings by day`() {

        val diaries = createDiaries(
            durationSpacing = DateTimeUnit.DAY,
            startDate = LocalDate.parse(isoString = "2023-05-01"),
            count = 3
        )

        val groups = diaries.groupByDate(clock)

        assertThat(groups.size).isEqualTo(3)
    }

    @Test
    fun `test diary groupings by week`() {
        val diaries = createDiaries(
            durationSpacing = DateTimeUnit.WEEK,
            startDate = LocalDate.parse(isoString = "2023-05-01"),
            count = 3
        )
        val groups = diaries.groupByDate(clock)

        assertThat(groups.size).isEqualTo(3)
    }

    @Test
    fun `test diary groupings by months`() {

        val diaries = createDiaries(
            durationSpacing = DateTimeUnit.MONTH,
            startDate = LocalDate.parse(isoString = "2023-01-01"),
            count = 3
        )
        val groups = diaries.groupByDate(clock)

        assertThat(groups.size).isEqualTo(3)
    }

    @Test
    fun `test diary groupings by months and weeks`() {
        val diaries = createDiaries(
            durationSpacing = DateTimeUnit.WEEK,
            startDate = LocalDate.parse(isoString = "2023-04-08"),
            count = 4
        )
        val groups = diaries.groupByDate(clock)

        assertThat(groups.size).isEqualTo(4)
    }

    private fun createDiaries(
        durationSpacing: DateTimeUnit.DateBased,
        startDate: LocalDate,
        count: Int,
    ): List<Diary> {
        return (0 until count).map {
            Diary(
                id = Random.nextLong(),
                entry = "Diary Entry #$it",
                date = startDate.plus(
                    value = it.toLong(),
                    unit = durationSpacing,
                ).toString(),
            )
        }
    }
}
