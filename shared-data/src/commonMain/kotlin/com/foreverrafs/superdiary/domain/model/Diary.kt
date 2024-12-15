package com.foreverrafs.superdiary.domain.model

import com.foreverrafs.superdiary.core.location.Location
import com.foreverrafs.superdiary.data.model.DiaryDto
import com.foreverrafs.superdiary.database.model.DiaryDb
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Diary(
    val entry: String,
    val id: Long? = null,
    val date: Instant = Clock.System.now(),
    val isFavorite: Boolean = false,
    val location: Location = Location.Empty,
)

fun Diary.toDto(): DiaryDto = DiaryDto(
    entry = entry,
    id = id,
    date = date,
    isFavorite = isFavorite,
    location = location.toString(),
)

fun Diary.toDatabase(): DiaryDb = DiaryDb(
    entry = entry,
    id = id,
    date = date,
    isFavorite = isFavorite,
    location = location.toString(),
)
