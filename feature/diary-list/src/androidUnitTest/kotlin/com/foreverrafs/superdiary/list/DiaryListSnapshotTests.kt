package com.foreverrafs.superdiary.list

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.foreverrafs.common.paparazzi.SnapshotDevice
import com.foreverrafs.superdiary.core.location.Location
import com.foreverrafs.superdiary.design.style.SuperDiaryPreviewTheme
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.list.presentation.DiaryListScreenContent
import com.foreverrafs.superdiary.list.presentation.DiaryListViewState
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class DiaryListSnapshotTests(
    @TestParameter val snapshotDevice: SnapshotDevice,
) {
    private val testClock = object : Clock {
        // 2023-11-10
        override fun now(): Instant = Instant.Companion.parse("2023-11-10T00:00:00.850951Z")
    }

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = snapshotDevice.config,
        renderingMode = SessionParams.RenderingMode.V_SCROLL,
        useDeviceResolution = true,
    )

    private val diaryListActions = DiaryListActions(
        onAddEntry = {},
        onDeleteDiaries = { true },
        onApplyFilters = {},
        onToggleFavorite = { false },
        onDiaryClicked = {},
    )

    private val deviceSize: DpSize
        @Composable
        get() {
            with(LocalDensity.current) {
                val deviceConfig = snapshotDevice.config
                return DpSize(
                    deviceConfig.screenWidth.toDp(),
                    deviceConfig.screenHeight.toDp(),
                )
            }
        }

    @Test
    fun `Loading diary list`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme(modifier = Modifier.Companion.size(deviceSize)) {
                DiaryListScreenContent(
                    state = DiaryListViewState.Loading,
                    showSearchBar = true,
                    diaryFilters = DiaryFilters(),
                    diaryListActions = diaryListActions,
                    avatarUrl = "",
                )
            }
        }
    }

    @Test
    fun `Unfiltered non-empty diary list`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme(modifier = Modifier.Companion.height(1500.dp)) {
                DiaryListScreenContent(
                    state = DiaryListViewState.Content(
                        (0..13).map {
                            Diary(
                                id = it.toLong(),
                                entry = "Hello Diary $it",
                                date = testClock.now().minus(
                                    value = 1,
                                    unit = DateTimeUnit.Companion.MONTH,
                                    timeZone = TimeZone.Companion.UTC,
                                ),
                                isFavorite = false,
                                location = Location.Companion.Empty,
                            )
                        },
                        filtered = false,
                    ),
                    clock = testClock,
                    showSearchBar = true,
                    diaryFilters = DiaryFilters(),
                    diaryListActions = diaryListActions,
                    avatarUrl = "",
                )
            }
        }
    }

    @Test
    fun `Unfiltered empty diary list`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme(modifier = Modifier.Companion.size(deviceSize)) {
                DiaryListScreenContent(
                    state = DiaryListViewState.Content(
                        listOf(),
                        filtered = false,
                    ),
                    showSearchBar = true,
                    diaryFilters = DiaryFilters(),
                    diaryListActions = diaryListActions,
                    avatarUrl = "",
                )
            }
        }
    }

    @Test
    fun `Filtered empty diary list`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme(modifier = Modifier.Companion.size(deviceSize)) {
                DiaryListScreenContent(
                    state = DiaryListViewState.Content(
                        listOf(),
                        filtered = true,
                    ),
                    showSearchBar = true,
                    diaryFilters = DiaryFilters(),
                    diaryListActions = diaryListActions,
                    avatarUrl = "",
                )
            }
        }
    }

    @Test
    fun `Error loading diary list`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme(modifier = Modifier.Companion.size(deviceSize)) {
                DiaryListScreenContent(

                    state = DiaryListViewState.Error(
                        Error("Error loading diaries"),
                    ),
                    showSearchBar = true,
                    diaryFilters = DiaryFilters(),
                    diaryListActions = diaryListActions,
                    avatarUrl = "",
                )
            }
        }
    }
}
