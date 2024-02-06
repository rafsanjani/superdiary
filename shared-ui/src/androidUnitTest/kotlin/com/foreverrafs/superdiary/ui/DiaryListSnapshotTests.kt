package com.foreverrafs.superdiary.ui

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.ui.feature.creatediary.screen.CreateDiaryScreenContent
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryFilters
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListActions
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListScreenContent
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListViewState
import com.foreverrafs.superdiary.ui.style.SuperdiaryAppTheme
import com.mohamedrejeb.richeditor.model.RichTextState
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest

class DiaryListSnapshotTests : KoinTest {
    private val testClock = object : Clock {
        // 2023-11-10
        override fun now(): Instant = Instant.parse("2023-11-10T00:00:00.850951Z")
    }

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        showSystemUi = true,
    )

    private val diaryListActions = DiaryListActions(
        onAddEntry = {},
        onDeleteDiaries = { true },
        onApplyFilters = {},
        onToggleFavorite = { false },
        onDiaryClicked = {},
    )

    @Test
    fun `Loading diary list`() {
        paparazzi.snapshot {
            SuperdiaryAppTheme {
                DiaryListScreenContent(
                    state = DiaryListViewState.Loading,
                    showSearchBar = true,
                    diaryFilters = DiaryFilters(),
                    diaryListActions = diaryListActions,
                )
            }
        }
    }

    @Test
    fun `Create diary entry`() {
        paparazzi.snapshot {
            SuperdiaryAppTheme {
                CreateDiaryScreenContent(
                    onNavigateBack = {},
                    onSaveDiary = {},
                    onGenerateAI = { _, _ -> },
                    isGeneratingFromAi = false,
                )
            }
        }
    }

    @Test
    fun `Create diary entry AI generated`() {
        paparazzi.snapshot {
            SuperdiaryAppTheme {
                CreateDiaryScreenContent(
                    richTextState = RichTextState().apply { setHtml("<p>AI generated diary content</p>") },
                    onNavigateBack = {},
                    onSaveDiary = {},
                    onGenerateAI = { _, _ -> },
                    isGeneratingFromAi = true,
                )
            }
        }
    }

    @Test
    fun `Unfiltered non-empty diary list`() {
        paparazzi.snapshot {
            SuperdiaryAppTheme {
                DiaryListScreenContent(
                    state = DiaryListViewState.Content(
                        (0..5).map {
                            Diary(
                                id = it.toLong(),
                                entry = "Hello Diary $it",
                                date = testClock.now().minus(
                                    value = 1,
                                    unit = DateTimeUnit.MONTH,
                                    timeZone = TimeZone.UTC,
                                ),
                                isFavorite = false,
                            )
                        },
                        filtered = false,
                    ),
                    clock = testClock,
                    showSearchBar = true,
                    diaryFilters = DiaryFilters(),
                    diaryListActions = diaryListActions,
                )
            }
        }
    }

    @Test
    fun `Unfiltered empty diary list`() {
        paparazzi.snapshot {
            SuperdiaryAppTheme {
                DiaryListScreenContent(
                    state = DiaryListViewState.Content(
                        listOf(),
                        filtered = false,
                    ),
                    showSearchBar = true,
                    diaryFilters = DiaryFilters(),
                    diaryListActions = diaryListActions,
                )
            }
        }
    }

    @Test
    fun `Filtered empty diary list`() {
        paparazzi.snapshot {
            SuperdiaryAppTheme {
                DiaryListScreenContent(
                    state = DiaryListViewState.Content(
                        listOf(),
                        filtered = true,
                    ),
                    showSearchBar = true,
                    diaryFilters = DiaryFilters(),
                    diaryListActions = diaryListActions,
                )
            }
        }
    }

    @Test
    fun `Error loading diary list`() {
        paparazzi.snapshot {
            SuperdiaryAppTheme {
                DiaryListScreenContent(
                    state = DiaryListViewState.Error(
                        Error("Error loading diaries"),
                    ),
                    showSearchBar = true,
                    diaryFilters = DiaryFilters(),
                    diaryListActions = diaryListActions,
                )
            }
        }
    }
}
