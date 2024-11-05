package com.foreverrafs.superdiary.data.model

import com.foreverrafs.superdiary.core.location.Location
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Diary(
    val entry: String,
    val id: Long? = null,
    val date: Instant = Clock.System.now(),
    val isFavorite: Boolean = false,
    val location: Location = Location.Empty,
)
