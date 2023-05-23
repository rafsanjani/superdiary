package com.foreverrafs.superdiary.diary.utils

import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import kotlinx.datetime.todayIn

fun List<Diary>.groupByDate(clock: Clock = Clock.System): Map<PrioritizedDuration, List<Diary>> =
    groupBy { getDurationString(it, clock) }

/**
 * Durations are weighted from 0 - 4 with 0 being the most prioritized and
 * 4 being the least. This priority is used to determine how the grouped
 * dates will be sorted
 */
data class PrioritizedDuration(
    val label: String,
    val priority: Int
)

private fun getDurationString(diary: Diary, clock: Clock = Clock.System): PrioritizedDuration {
    val entryDate = LocalDate.parse(diary.date)

    val difference = entryDate.periodUntil(clock.todayIn(TimeZone.currentSystemDefault()))

    if (difference.days == 0 && difference.months == 0 && difference.years == 0) {
        return PrioritizedDuration(
            label = "Today",
            priority = 0
        )
    }

    if (difference.days < 7 && difference.months == 0 && difference.years == 0) {
        val days = difference.days
        return PrioritizedDuration(
            label = "$days day${if (days > 1) "s" else ""} ago",
            priority = 1
        )
    }

    if (difference.months < 1 && difference.years == 0) {
        val weeks = difference.days / 7
        return PrioritizedDuration(
            label = "$weeks week${if (weeks > 1) "s" else ""} ago",
            priority = 2
        )
    }

    if (difference.years < 1) {
        val months = difference.months
        return PrioritizedDuration(
            label = "$months month${if (months > 1) "s" else ""} ago",
            priority = 3
        )
    }

    return PrioritizedDuration(
        label = "${difference.years} year(s) ago",
        priority = 4
    )
}
