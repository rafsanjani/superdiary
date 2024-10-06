package com.foreverrafs.superdiary.data.model

import com.foreverrafs.superdiary.core.location.Location
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Diary(
    val entry: String,
    val id: Long? = null,
    @kotlinx.serialization.Transient
    val date: Instant = Clock.System.now(),
    val isFavorite: Boolean = false,
    val location: Location = Location.Empty,
)
