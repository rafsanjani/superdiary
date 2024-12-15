package com.foreverrafs.superdiary.database.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class WeeklySummaryDb(
    val summary: String,
    val date: Instant = Clock.System.now(),
)
