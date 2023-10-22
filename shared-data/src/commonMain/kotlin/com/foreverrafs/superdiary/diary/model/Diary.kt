package com.foreverrafs.superdiary.diary.model

import kotlinx.datetime.Instant

data class Diary(
    val id: Long? = null,
    val entry: String,
    val date: Instant,
    val isFavorite: Boolean,
)
