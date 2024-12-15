package com.foreverrafs.superdiary.data.mapper

import com.foreverrafs.superdiary.core.location.Location
import com.foreverrafs.superdiary.data.diaryai.DiaryChatMessage
import com.foreverrafs.superdiary.data.diaryai.DiaryChatRole
import com.foreverrafs.superdiary.database.model.DiaryChatMessageDb
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
)

fun WeeklySummaryDb.toWeeklySummary(): WeeklySummary = WeeklySummary(
    summary = summary,
    date = date,
)

fun DiaryChatMessageDb.toDiaryChatMessage(): DiaryChatMessage = DiaryChatMessage(
    id = id,
    role = DiaryChatRole.valueOf(role.name),
    timestamp = timestamp,
    content = content,
)
