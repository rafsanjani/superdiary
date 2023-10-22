package com.foreverrafs.superdiary.diary.usecase

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isInstanceOf
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.utils.DiaryValidator
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.test.Test

class DiaryValidatorTest {
    private val testClock: Clock = object : Clock {
        override fun now(): Instant = Instant.parse("2023-05-01T01:01:01.049Z")
    }

    private val validator: DiaryValidator = DiaryValidator(clock = testClock)

    @Test
    fun `fail validation for past date`() {
        val diary = Diary(
            entry = "Bad Diary",
            date = testClock.now().minus(
                DateTimePeriod(
                    days = 1,
                ),
                TimeZone.UTC,
            ),
            isFavorite = false,
        )

        assertFailure {
            validator.validate(diary)
        }.isInstanceOf<IllegalArgumentException>()
    }

    @Test
    fun `fail validation for future date`() {
        val diary = Diary(
            entry = "Bad Diary",
            date = testClock.now().plus(
                DateTimePeriod(
                    days = 1,
                ),
                TimeZone.UTC,
            ),
            isFavorite = false,
        )

        assertFailure {
            validator.validate(diary)
        }.isInstanceOf<IllegalArgumentException>()
    }

    @Test
    fun `pass validation for today's date`() {
        val diary = Diary(
            entry = "Bad Diary",
            date = testClock.now(),
            isFavorite = false,
        )

        assertThat(validator.validate(diary)).isInstanceOf<Unit>()
    }
}
