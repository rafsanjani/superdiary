package com.foreverrafs.superdiary

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.ui.format
import kotlinx.datetime.LocalDate
import kotlin.test.Test

class DateFormatterTest {
    @Test
    fun `Verify date formatting works properly`() {
        val date = LocalDate(2020, 10, 10)
        val formattedDate = date.format("YYYY")

        assertThat(formattedDate).isEqualTo("2020")
    }
}
