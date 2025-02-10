package com.foreverrafs.superdiary.common.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

expect fun LocalDate.format(format: String): String
expect fun LocalDateTime.format(format: String): String
