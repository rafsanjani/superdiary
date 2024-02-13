package com.foreverrafs.superdiary.ui

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toNSDate
import kotlinx.datetime.toNSTimeZone
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.dateWithTimeIntervalSince1970

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
    val formatter = NSDateFormatter().apply {
        dateFormat = format
    }

    val date = NSDate.dateWithTimeIntervalSince1970(
        toInstant(TimeZone.UTC).epochSeconds.toDouble(),
    )

    return formatter.stringFromDate(
        date,
    )
}
