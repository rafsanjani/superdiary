package com.foreverrafs.superdiary.ui

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.ui.components.DiaryListScreen
import com.foreverrafs.superdiary.ui.screens.DiaryScreenState
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest

class DiaryListSnapshotTests : KoinTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        showSystemUi = true,
    )

    @Test
    fun nonEmptyDiaryList() {
        paparazzi.snapshot {
            TestAppContainer {
                DiaryListScreen(
                    state = DiaryScreenState.Content(
                        (0..5).map {
                            Diary(
                                id = it.toLong(),
                                entry = "Hello Diary $it",
                                date = Clock.System.now().toLocalDateTime(
                                    timeZone = TimeZone.UTC,
                                ).date.toString(),
                            )
                        },
                    ),
                )
            }
        }
    }

    @Test
    fun emptyDiaryList() {
        paparazzi.snapshot {
            TestAppContainer {
                DiaryListScreen(
                    state = DiaryScreenState.Content(
                        listOf(),
                    ),
                )
            }
        }
    }
}
