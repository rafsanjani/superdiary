package com.foreverrafs.superdiary.common.utils

import java.time.format.DateTimeFormatter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime

actual fun LocalDate.format(format: String): String =
    DateTimeFormatter.ofPattern(format).format(this.toJavaLocalDate())

actual fun LocalDateTime.format(format: String): String =
    DateTimeFormatter.ofPattern(format).format(this.toJavaLocalDateTime())
