package com.foreverrafs.superdiary.util

import com.foreverrafs.domain.feature_diary.model.Diary
import java.time.LocalDateTime

object TestData {
    val testDiaries = listOf(
        Diary(
            id = 1L,
            title = "Test Diary #1",
            message = "Test Diary Message #1",
            date = LocalDateTime.parse("2022-01-01T18:14:47.119")
        ),
        Diary(
            id = 2L,
            title = "Test Diary #2",
            message = "Test Diary Message #2",
            date = LocalDateTime.parse("2022-01-01T19:14:47.119")
        ),
        Diary(
            id = 1L,
            title = "Test Diary #3",
            message = "Test Diary Message #3",
            date = LocalDateTime.parse("2022-01-01T20:14:47.119")
        ),
        Diary(
            id = 2L,
            title = "Test Diary #4",
            message = "Test Diary Message #4",
            date = LocalDateTime.parse("2022-01-01T21:14:47.119")
        )
    )
}