package com.foreverrafs.superdiary.database.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class DiaryDb(
    val entry: String,
    val id: Long? = null,
    val date: Instant = Clock.System.now(),
    val isFavorite: Boolean = false,
    val location: String,
    val markedForDelete: Boolean,
)
