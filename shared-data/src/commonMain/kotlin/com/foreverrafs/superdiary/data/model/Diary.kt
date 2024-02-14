package com.foreverrafs.superdiary.data.model

import com.foreverrafs.superdiary.data.serializable.JavaSerializable
import kotlin.jvm.Transient
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Diary(
    val entry: String,
    val id: Long? = null,
    @Transient
    val date: Instant = Clock.System.now(),
    val isFavorite: Boolean = false,
) : JavaSerializable
