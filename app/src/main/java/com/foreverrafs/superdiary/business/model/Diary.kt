package com.foreverrafs.superdiary.business.model

import android.graphics.Color
import java.time.LocalDateTime

data class Diary(
    val id: Long = 0,
    val message: String,
    val date: LocalDateTime = LocalDateTime.now(),
    val eventColor: Int = Color.GRAY,
)
