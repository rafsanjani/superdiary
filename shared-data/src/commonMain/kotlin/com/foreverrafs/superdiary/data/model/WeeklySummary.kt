package com.foreverrafs.superdiary.data.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class WeeklySummary(
    val summary: String,
    val date: Instant = Clock.System.now(),
)
