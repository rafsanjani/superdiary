package com.foreverrafs.superdiary.ui

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListScreen
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListScreenState
import kotlinx.datetime.LocalDate
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
        val date = LocalDate.parse("2023-10-10")

        paparazzi.snapshot {
            TestAppContainer {
                DiaryListScreen(
                    state = DiaryListScreenState.Content(
                        (0..5).map {
                            Diary(
                                id = it.toLong(),
                                entry = "Hello Diary $it",
                                date = date.toString(),
                            )
                        },
                    ),
                    onAddEntry = {},
                )
            }
        }
    }

    @Test
    fun emptyDiaryList() {
        paparazzi.snapshot {
            TestAppContainer {
                DiaryListScreen(
                    state = DiaryListScreenState.Content(
                        listOf(),
                    ),
                    onAddEntry = {},
                )
            }
        }
    }

    @Test
    fun errorLoadingDiaries() {
        paparazzi.snapshot {
            TestAppContainer {
                DiaryListScreen(
                    state = DiaryListScreenState.Error(
                        Error("Error loading diaries"),
                    ),
                    onAddEntry = {},
                )
            }
        }
    }
}
