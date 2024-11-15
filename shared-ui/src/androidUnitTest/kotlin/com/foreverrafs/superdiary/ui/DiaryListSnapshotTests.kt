package com.foreverrafs.superdiary.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.foreverrafs.superdiary.core.location.Location
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryFilters
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListActions
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListScreenContent
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListViewState
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
        override fun now(): Instant = Instant.parse("2023-11-10T00:00:00.850951Z")
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
            SuperdiaryPreviewTheme(modifier = Modifier.size(deviceSize)) {
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
    fun `Unfiltered non-empty diary list`() {
        paparazzi.snapshot {
            SuperdiaryPreviewTheme(modifier = Modifier.height(1500.dp)) {
                DiaryListScreenContent(
                    state = DiaryListViewState.Content(
                        (0..13).map {
                            Diary(
                                id = it.toLong(),
                                entry = "Hello Diary $it",
                                date = testClock.now().minus(
                                    value = 1,
                                    unit = DateTimeUnit.MONTH,
                                    timeZone = TimeZone.UTC,
                                ),
                                isFavorite = false,
                                location = Location.Empty,
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
            SuperdiaryPreviewTheme(modifier = Modifier.size(deviceSize)) {
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
            SuperdiaryPreviewTheme(modifier = Modifier.size(deviceSize)) {
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
            SuperdiaryPreviewTheme(modifier = Modifier.size(deviceSize)) {
                Text("Sample text")
            }
        }
    }
}
