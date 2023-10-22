package com.foreverrafs.superdiary.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryFilters
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListScreen
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListScreenState
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest

class DiaryListSnapshotTests : KoinTest {
    private val testClock = object : Clock {
        // 2023-10-10
        override fun now(): Instant = Instant.fromEpochSeconds(1697710617)
    }

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        showSystemUi = true,
    )

    @Test
    fun `Loading diary list`() {
        paparazzi.snapshot {
            TestAppContainer {
                DiaryListScreen(
                    state = DiaryListScreenState.Loading,
                    onAddEntry = {},
                    onApplyFilters = {},
                    diaryFilters = DiaryFilters(),
                    onDeleteDiaries = {},
                    snackbarHostState = remember {
                        SnackbarHostState()
                    },
                    onToggleFavorite = {},
                )
            }
        }
    }

    @Test
    fun `Unfiltered non-empty diary list`() {
        paparazzi.snapshot {
            TestAppContainer {
                DiaryListScreen(
                    state = DiaryListScreenState.Content(
                        (0..5).map {
                            Diary(
                                id = it.toLong(),
                                entry = "Hello Diary $it",
                                date = testClock.now(),
                                isFavorite = false,
                            )
                        },
                        filtered = false,
                    ),
                    onAddEntry = {},
                    onApplyFilters = {},
                    diaryFilters = DiaryFilters(),
                    onDeleteDiaries = {},
                    snackbarHostState = remember {
                        SnackbarHostState()
                    },
                    onToggleFavorite = {},
                )
            }
        }
    }

    @Test
    fun `Unfiltered empty diary list`() {
        paparazzi.snapshot {
            TestAppContainer {
                DiaryListScreen(
                    state = DiaryListScreenState.Content(
                        listOf(),
                        filtered = false,
                    ),
                    onAddEntry = {},
                    onApplyFilters = {},
                    diaryFilters = DiaryFilters(),
                    onDeleteDiaries = {},
                    snackbarHostState = remember {
                        SnackbarHostState()
                    },
                    onToggleFavorite = {},
                )
            }
        }
    }

    @Test
    fun `Filtered empty diary list`() {
        paparazzi.snapshot {
            TestAppContainer {
                DiaryListScreen(
                    state = DiaryListScreenState.Content(
                        listOf(),
                        filtered = true,
                    ),
                    onAddEntry = {},
                    onApplyFilters = {},
                    diaryFilters = DiaryFilters(),
                    onDeleteDiaries = {},
                    snackbarHostState = remember {
                        SnackbarHostState()
                    },
                    onToggleFavorite = {},
                )
            }
        }
    }

    @Test
    fun `Error loading diary list`() {
        paparazzi.snapshot {
            TestAppContainer {
                DiaryListScreen(
                    state = DiaryListScreenState.Error(
                        Error("Error loading diaries"),
                    ),
                    onAddEntry = {},
                    onApplyFilters = {},
                    diaryFilters = DiaryFilters(),
                    onDeleteDiaries = {},
                    snackbarHostState = remember {
                        SnackbarHostState()
                    },
                    onToggleFavorite = {},
                )
            }
        }
    }
}
