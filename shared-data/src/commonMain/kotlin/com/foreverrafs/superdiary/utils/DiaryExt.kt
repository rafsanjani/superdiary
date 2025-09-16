package com.foreverrafs.superdiary.utils

import com.foreverrafs.superdiary.domain.model.Diary
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.periodUntil
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

fun List<Diary>.groupByDate(clock: Clock = Clock.System): Map<PrioritizedDuration, List<Diary>> =
    groupBy { getDurationString(it, clock) }
        .toList()
        .sortedBy { it.first.priority }
        .toMap()

/**
 * Durations are weighted from 0 - 4 with 0 being the most prioritized and
 * 4 being the least. This priority is used to determine how the grouped
 * dates will be sorted
 */
data class PrioritizedDuration(
    val label: String,
    val priority: Int,
)

@Suppress("ReturnCount", "CyclomaticComplexMethod")
private fun getDurationString(diary: Diary, clock: Clock = Clock.System): PrioritizedDuration {
    val entryDate = diary.date.toLocalDateTime(TimeZone.UTC).date

    val difference = entryDate.periodUntil(clock.todayIn(TimeZone.currentSystemDefault()))

    if (difference.days == 0 && difference.months == 0 && difference.years == 0) {
        return PrioritizedDuration(
            label = "Today",
            priority = 0,
        )
    }

    if (difference.days == 1 && difference.months == 0 && difference.years == 0) {
        return PrioritizedDuration(
            label = "Yesterday",
            priority = 1,
        )
    }

    if (difference.days < 7 && difference.months == 0 && difference.years == 0) {
        val days = difference.days
        return PrioritizedDuration(
            label = "$days day${if (days > 1) "s" else ""} ago",
            priority = days + 1,
        )
    }

    if (difference.months < 1 && difference.years == 0) {
        val weeks = difference.days / 7
        return PrioritizedDuration(
            label = "$weeks week${if (weeks > 1) "s" else ""} ago",
            priority = 7 * weeks,
        )
    }

    if (difference.years < 1) {
        val months = difference.months
        return PrioritizedDuration(
            label = "$months month${if (months > 1) "s" else ""} ago",
            priority = 30 * months,
        )
    }

    return PrioritizedDuration(
        label = "${difference.years} year(s) ago",
        priority = 360 * difference.years,
    )
}

fun Instant.toDate(): LocalDate = toLocalDateTime(TimeZone.UTC).date
fun LocalDate.toInstant(): Instant = atStartOfDayIn(TimeZone.UTC)
