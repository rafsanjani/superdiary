package com.foreverrafs.superdiary.dashboard

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import kotlin.test.Test
import kotlinx.datetime.LocalDate

class DateUtilsTest {

    @Test
    fun `should return true when dates are in the same week`() {
        val date1 = LocalDate(2023, 10, 2) // Monday
        val date2 = LocalDate(2023, 10, 5) // Thursday

        assertThat(date1.isInSameWeekAs(date2))
            .isTrue()
    }

    @Test
    fun `should return false when dates are in adjacent weeks`() {
        val date1 = LocalDate(2023, 10, 1) // Sunday
        val date2 = LocalDate(2023, 10, 2) // Monday
        assertThat(date1.isInSameWeekAs(date2))
            .isFalse()
    }

    @Test
    fun `should return false when dates are in different weeks across months`() {
        val date1 = LocalDate(2023, 10, 31) // Tuesday
        val date2 = LocalDate(2023, 11, 1) // Wednesday
        assertThat(date1.isInSameWeekAs(date2))
            .isTrue()
    }

    @Test
    fun `should return false when dates are in different years`() {
        val date1 = LocalDate(2023, 12, 30) // Saturday
        val date2 = LocalDate(2024, 1, 1) // Monday
        assertThat(date1.isInSameWeekAs(date2))
            .isFalse()
    }

    @Test
    fun `should return true when a week spans two months`() {
        val date1 = LocalDate(2023, 1, 30) // Monday
        val date2 = LocalDate(2023, 2, 2) // Thursday
        assertThat(date1.isInSameWeekAs(date2))
            .isTrue()
    }

    @Test
    fun `should return true for the same date`() {
        val date1 = LocalDate(2023, 1, 15)
        val date2 = LocalDate(2023, 1, 15)
        assertThat(date1.isInSameWeekAs(date2))
            .isTrue()
    }
}
