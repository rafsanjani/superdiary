package com.foreverrafs.superdiary.dashboard

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus

fun LocalDate.isInSameWeekAs(date: LocalDate): Boolean {
    val startOfWeek1 = this.minus(this.dayOfWeek.isoDayNumber - DayOfWeek.MONDAY.isoDayNumber, DateTimeUnit.DAY)
    val startOfWeek2 = date.minus(date.dayOfWeek.isoDayNumber - DayOfWeek.MONDAY.isoDayNumber, DateTimeUnit.DAY)

    return startOfWeek1 == startOfWeek2
}
