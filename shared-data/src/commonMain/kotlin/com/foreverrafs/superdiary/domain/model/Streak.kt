package com.foreverrafs.superdiary.domain.model

import kotlinx.datetime.LocalDate

/** A streak represents consecutive entries from today inclusive */
data class Streak(
    val count: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
)
