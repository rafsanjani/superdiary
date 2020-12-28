package com.foreverrafs.superdiary.business.model

import android.graphics.Color
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Diary(
    val id: Long = 0,
    val message: String,
    val date: LocalDateTime = LocalDateTime.now(),
    val eventColor: Int = Color.GRAY,
) : Parcelable
