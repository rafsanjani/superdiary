package com.foreverrafs.superdiary.data.model

import com.foreverrafs.superdiary.core.location.Location
import com.foreverrafs.superdiary.database.model.DiaryDb
import com.foreverrafs.superdiary.domain.model.Diary
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiaryDto(
    @SerialName("entry")
    val entry: String,
    @SerialName("id")
    val id: Long? = null,
    @SerialName("date")
    val date: Instant = Clock.System.now(),
    @SerialName("isFavorite")
    val isFavorite: Boolean = false,
    @SerialName("location")
    val location: String? = Location.Empty.toString(),
)

fun DiaryDto.toDiary(): Diary = Diary(
    entry = entry,
    id = id,
    date = date,
    isFavorite = isFavorite,
    location = Location.fromString(location ?: Location.Empty.toString()),
    // a fresh entry from the network should be considered synced
    isSynced = true,
    isMarkedForDelete = false,
)

fun DiaryDto.toDatabase(): DiaryDb = DiaryDb(
    entry = entry,
    id = id,
    date = date,
    isFavorite = isFavorite,
    location = Location.toString(),
    markedForDelete = false,
    isSynced = true,
)
