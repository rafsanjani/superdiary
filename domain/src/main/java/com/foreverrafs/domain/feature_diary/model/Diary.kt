package com.foreverrafs.domain.feature_diary.model

import android.graphics.Color
import java.time.LocalDateTime

data class Diary(
    val title: String,
    val message: String,
    val date: LocalDateTime = LocalDateTime.now(),
    val eventColor: Int = Color.GRAY,
)
