package com.foreverrafs.superdiary.data

import com.foreverrafs.domain.business.model.Diary
import java.time.LocalDateTime
import kotlin.random.Random


val mockDiaries = mutableListOf(
    com.foreverrafs.domain.business.model.Diary(
        id = Random.nextLong(),
        message = "Mock dairy entry one",
        date = LocalDateTime.now().minusDays(2),
    ),
    com.foreverrafs.domain.business.model.Diary(
        id = Random.nextLong(),
        message = "Mock dairy entry two",
        date = LocalDateTime.now().minusDays(2),
    ),
    com.foreverrafs.domain.business.model.Diary(
        id = Random.nextLong(),
        message = "Mock dairy entry three",
        date = LocalDateTime.now().minusDays(1),
    ),
    com.foreverrafs.domain.business.model.Diary(
        id = Random.nextLong(),
        message = "Mock dairy entry four",
        date = LocalDateTime.now().minusDays(1),
    ),
    com.foreverrafs.domain.business.model.Diary(
        id = Random.nextLong(),
        message = "Mock dairy entry five",
        date = LocalDateTime.now(),
    ),
    com.foreverrafs.domain.business.model.Diary(
        id = Random.nextLong(),
        message = "Mock dairy entry six",
        date = LocalDateTime.now(),
    ),
    com.foreverrafs.domain.business.model.Diary(
        id = Random.nextLong(),
        message = "Mock dairy entry seven",
        date = LocalDateTime.now(),
    )
)