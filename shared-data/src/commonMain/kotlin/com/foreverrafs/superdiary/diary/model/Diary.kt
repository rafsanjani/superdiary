package com.foreverrafs.superdiary.diary.model

import com.foreverrafs.superdiary.diary.serializable.JavaSerializable
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.jvm.Transient

@Suppress("FunctionName")
data class Diary(
    val entry: String,
    val id: Long? = null,
    @Transient
    val date: Instant = Clock.System.now(),
    val isFavorite: Boolean = false,
) : JavaSerializable
