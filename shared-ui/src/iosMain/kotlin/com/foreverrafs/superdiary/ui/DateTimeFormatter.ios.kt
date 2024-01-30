package com.foreverrafs.superdiary.ui

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toNSDate
import kotlinx.datetime.toNSDateComponents
import kotlinx.datetime.toNSTimeZone
import platform.Foundation.NSDateFormatter

actual fun LocalDate.format(format: String): String {
    val formatter = NSDateFormatter().apply {
        dateFormat = format
        timeZone = TimeZone.currentSystemDefault().toNSTimeZone()
    }

    return formatter.stringFromDate(
        atStartOfDayIn(TimeZone.currentSystemDefault()).toNSDate(),
    )
}

actual fun LocalDateTime.format(format: String): String {
    val dateFormatter = NSDateFormatter().apply {
        dateFormat = format
    }
    return dateFormatter.stringFromDate(
        toNSDateComponents().date
            ?: throw IllegalStateException("Could not convert kotlin date to NSDate $this")
    )
}
