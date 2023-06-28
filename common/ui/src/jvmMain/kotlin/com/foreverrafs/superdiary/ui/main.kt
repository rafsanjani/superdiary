package com.foreverrafs.superdiary.ui // ktlint-disable filename

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.singleWindowApplication
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.ui.components.DiaryList
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun main() = singleWindowApplication {
    AppTheme {
        DiaryListScreen()
    }
}

@Composable
@Preview
fun DiaryListScreen() {
    DiaryList(
        diaries = listOf(
            Diary(
                date = Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date.toString(),
                entry = "Hello World",
            ),
        ),
    )
}
