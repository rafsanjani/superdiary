package com.foreverrafs.superdiary.diary.model

import com.foreverrafs.superdiary.diary.parcel.CommonParcelable
import com.foreverrafs.superdiary.diary.parcel.CommonParcelize
import com.foreverrafs.superdiary.diary.parcel.CommonRawValue
import kotlinx.datetime.Instant

@CommonParcelize
data class Diary(
    val id: Long? = null,
    val entry: String,
    val date: @CommonRawValue Instant,
    val isFavorite: Boolean,
) : CommonParcelable
