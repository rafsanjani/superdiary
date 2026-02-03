package com.foreverrafs.superdiary.domain.model

import com.foreverrafs.superdiary.core.location.Location
import com.foreverrafs.superdiary.data.model.DiaryDto
import com.foreverrafs.superdiary.database.model.DiaryDb
import kotlin.time.Clock
import kotlin.time.Instant

data class Diary(
    val entry: String,
    val id: Long? = null,
    val date: Instant = Clock.System.now(),
    val isFavorite: Boolean = false,
    val location: Location = Location.Empty,
    // An entry marked for deletion will not be rendered and will be removed
    // from remote db during next sync cycle
    val isMarkedForDelete: Boolean = false,
    val isSynced: Boolean = false,
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
