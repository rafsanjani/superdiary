package com.foreverrafs.superdiary.database.model

import kotlin.time.Clock
import kotlin.time.Instant

data class DiaryDb(
    val entry: String,
    val id: Long? = null,
    val date: Instant = Clock.System.now(),
    val isFavorite: Boolean = false,
    val location: String,
    val markedForDelete: Boolean,
    val isSynced: Boolean,
)
