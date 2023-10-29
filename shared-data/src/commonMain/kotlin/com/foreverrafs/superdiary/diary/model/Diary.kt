package com.foreverrafs.superdiary.diary.model

import com.foreverrafs.superdiary.diary.parcel.CommonTypeParceler
import com.foreverrafs.superdiary.diary.parcel.InstantParceler
import com.foreverrafs.superdiary.diary.parcel.JavaSerializable
import kotlinx.datetime.Instant
import kotlin.jvm.Transient

data class Diary(
    val id: Long? = null,
    val entry: String,
    @Transient
    @CommonTypeParceler<Instant, InstantParceler>
    val date: Instant,
    val isFavorite: Boolean,
) : JavaSerializable
