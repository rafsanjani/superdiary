package com.foreverrafs.superdiary.ui

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter

actual fun LocalDate.format(format: String): String {
    return DateTimeFormatter.ofPattern(format).format(this.toJavaLocalDate())
}

actual fun LocalDateTime.format(format: String): String {
    return DateTimeFormatter.ofPattern(format).format(this.toJavaLocalDateTime())
}