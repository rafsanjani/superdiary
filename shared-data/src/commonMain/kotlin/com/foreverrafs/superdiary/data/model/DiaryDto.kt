package com.foreverrafs.superdiary.data.model

import com.foreverrafs.superdiary.core.location.Location
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
    val location: String,
)

fun DiaryDto.toDiary(): Diary = Diary(
    entry = entry,
    id = id,
    date = date,
    isFavorite = isFavorite,
    location = Location.fromString(location),
)
