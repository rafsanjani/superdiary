package com.foreverrafs.superdiary.diary.utils

import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil

fun List<Diary>.groupByDate(clock: Clock = Clock.System): Map<String, List<Diary>> =
    groupBy { getDurationString(it, clock) }

private fun getDurationString(diary: Diary, clock: Clock = Clock.System): String {
    val entryDate = Instant.parse(diary.date)

    val difference = entryDate.periodUntil(clock.now(), TimeZone.currentSystemDefault())

    if (difference.days == 0 && difference.months == 0 && difference.years == 0) {
        return "Today"
    }

    if (difference.days < 7 && difference.months == 0 && difference.years == 0) {
        return "${difference.days} days ago..."
    }

    if (difference.months < 1 && difference.years == 0) {
        val weeks = difference.days / 7
        return "$weeks week${if (weeks > 1) "s" else ""} ago..."
    }

    if (difference.years < 1) {
        val months = difference.months
        return "$months month${if (months > 1) "s" else ""} ago..."
    }

    return "${difference.years} year(s) ago..."
}
