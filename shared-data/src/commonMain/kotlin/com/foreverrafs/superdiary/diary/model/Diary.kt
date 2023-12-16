package com.foreverrafs.superdiary.diary.model

import com.foreverrafs.superdiary.diary.serializable.JavaSerializable
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.jvm.Transient

@Suppress("FunctionName")
data class Diary(
    val id: Long? = null,
    val entry: String,
    @Transient
    val date: Instant,
    val isFavorite: Boolean,
) : JavaSerializable {
    companion object {
        fun Now(entry: String): Diary =
            Diary(entry = entry, date = Clock.System.now(), isFavorite = false)
    }
}
