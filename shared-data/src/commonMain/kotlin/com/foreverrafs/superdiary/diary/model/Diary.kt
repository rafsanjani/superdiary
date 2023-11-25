package com.foreverrafs.superdiary.diary.model

import com.foreverrafs.superdiary.diary.serializable.JavaSerializable
import kotlinx.datetime.Instant
import kotlin.jvm.Transient

data class Diary(
    val id: Long? = null,
    val entry: String,
    @Transient
    val date: Instant,
    val isFavorite: Boolean,
) : JavaSerializable
