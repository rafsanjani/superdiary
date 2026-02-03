package com.foreverrafs.superdiary.data.model

import com.foreverrafs.superdiary.core.location.Location
import com.foreverrafs.superdiary.database.model.DiaryDb
import com.foreverrafs.superdiary.domain.model.Diary
import kotlin.time.Clock
import kotlin.time.Instant
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
    @SerialName("favorite")
    val isFavorite: Boolean = false,
    @SerialName("location")
    val location: String? = Location.Empty.toString(),
    @SerialName("updated_at")
    val updatedAt: Instant = date,
    @SerialName("is_deleted")
    val isDeleted: Boolean = false,
)

fun DiaryDto.toDiary(): Diary = Diary(
    entry = entry,
    id = id,
    date = date,
    isFavorite = isFavorite,
    location = Location.fromString(location ?: Location.Empty.toString()),
    updatedAt = updatedAt,
    // a fresh entry from the network should be considered synced
    isSynced = true,
    isMarkedForDelete = isDeleted,
)

fun DiaryDto.toDatabase(): DiaryDb = DiaryDb(
    entry = entry,
    id = id,
    date = date,
    isFavorite = isFavorite,
    location = Location.toString(),
    updatedAt = updatedAt,
    isSynced = true,
    isMarkedForDelete = isDeleted,
)
