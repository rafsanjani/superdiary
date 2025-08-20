package com.foreverrafs.superdiary.domain.model

import com.foreverrafs.superdiary.database.model.WeeklySummaryDb
import kotlin.time.Clock
import kotlin.time.Instant

data class WeeklySummary(
    val summary: String,
    val date: Instant = Clock.System.now(),
)

fun WeeklySummary.toDatabase(): WeeklySummaryDb = WeeklySummaryDb(
    summary = summary,
    date = date,
)
