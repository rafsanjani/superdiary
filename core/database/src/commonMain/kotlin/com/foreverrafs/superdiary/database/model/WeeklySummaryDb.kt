package com.foreverrafs.superdiary.database.model

import kotlin.time.Clock

data class WeeklySummaryDb(
    val summary: String,
    val date: kotlin.time.Instant = Clock.System.now(),
)
