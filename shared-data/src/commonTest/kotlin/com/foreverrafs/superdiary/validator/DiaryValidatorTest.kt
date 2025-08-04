package com.foreverrafs.superdiary.validator

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isInstanceOf
import com.foreverrafs.superdiary.core.location.Location
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.validator.DiaryValidator
import com.foreverrafs.superdiary.domain.validator.DiaryValidatorImpl
import kotlin.test.Test
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class DiaryValidatorTest {
    private val testClock: Clock = object : Clock {
        override fun now(): kotlin.time.Instant =
            kotlin.time.Instant.parse("2023-05-01T01:01:01.049Z")
    }

    private val validator: DiaryValidator = DiaryValidatorImpl(clock = testClock)

    @Test
    fun `Should fail validation for past date`() {
        val diary = Diary(
            entry = "Bad Diary",
            date = testClock.now().minus(
                DateTimePeriod(
                    days = 1,
                ),
                TimeZone.UTC,
            ),
            isFavorite = false,
            location = Location.Empty,
        )

        assertFailure {
            validator.validate(diary)
        }.isInstanceOf<IllegalArgumentException>()
    }

    @Test
    fun `Should fail validation for future date`() {
        val diary = Diary(
            entry = "Bad Diary",
            date = testClock.now().plus(
                DateTimePeriod(
                    days = 1,
                ),
                TimeZone.UTC,
            ),
            isFavorite = false,
            location = Location.Empty,
        )

        assertFailure {
            validator.validate(diary)
        }.isInstanceOf<IllegalArgumentException>()
    }

    @Test
    fun `Should pass validation for today's date`() {
        val diary = Diary(
            entry = "Bad Diary",
            date = testClock.now(),
            isFavorite = false,
            location = Location.Empty,
        )

        assertThat(validator.validate(diary)).isInstanceOf<Unit>()
    }
}
