package com.foreverrafs.superdiary.data.mapper

import com.foreverrafs.superdiary.core.location.Location
import com.foreverrafs.superdiary.database.model.DiaryDb
import com.foreverrafs.superdiary.database.model.WeeklySummaryDb
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.WeeklySummary

fun DiaryDb.toDiary(): Diary = Diary(
    id = id,
    entry = entry,
    date = date,
    isFavorite = isFavorite,
    location = Location.fromString(location),
    isMarkedForDelete = markedForDelete,
)

fun WeeklySummaryDb.toWeeklySummary(): WeeklySummary = WeeklySummary(
    summary = summary,
    date = date,
)
