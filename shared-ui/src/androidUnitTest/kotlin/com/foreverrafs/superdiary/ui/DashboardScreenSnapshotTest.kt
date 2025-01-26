package com.foreverrafs.superdiary.ui

import app.cash.paparazzi.Paparazzi
import com.foreverrafs.common.paparazzi.SnapshotDevice
import com.foreverrafs.superdiary.core.location.Location
import com.foreverrafs.superdiary.design.style.SuperDiaryPreviewTheme
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.Streak
import com.foreverrafs.superdiary.ui.feature.dashboard.DashboardViewModel
import com.foreverrafs.superdiary.ui.feature.dashboard.screen.DashboardScreenContent
import com.foreverrafs.superdiary.utils.toDate
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class DashboardScreenSnapshotTest(
    @TestParameter val snapshotDevice: SnapshotDevice,
) {
    private val testClock = object : Clock {
        // 2023-11-10
        override fun now(): Instant = Instant.parse("2023-11-10T00:00:00.850951Z")
    }

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = snapshotDevice.config,
        useDeviceResolution = true,
    )

    @Test
    fun `Dashboard Screen - Latest Entries`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme {
                DashboardScreenContent(
                    state = DashboardViewModel.DashboardScreenState.Content(
                        (0..3).map {
                            Diary(
                                id = it.toLong(),
                                entry = "<strong>Awesome</strong> Diary",
                                date = testClock.now(),
                                isFavorite = false,
                                location = Location.Empty,
                            )
                        },
                        3,
                        "This is the weekly summary of all the entries",
                        Streak(
                            0,
                            testClock.now().toDate(),
                            testClock.now().toDate(),
                        ),
                        bestStreak = Streak(
                            0,
                            testClock.now().toDate(),
                            testClock.now().toDate(),
                        ),
                    ),
                    onAddEntry = {},
                    onSeeAll = {},
                    onToggleFavorite = {},
                    onDiaryClick = {},
                    onDisableBiometricAuth = {},
                    onEnableBiometric = {},
                    onToggleLatestEntries = {},
                    onToggleWeeklySummaryCard = {},
                    onToggleGlanceCard = {},
                )
            }
        }
    }

    @Test
    fun `Dashboard Screen - Show biometric dialog when showBiometricAuthDialog is set to true`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme {
                DashboardScreenContent(
                    state = DashboardViewModel.DashboardScreenState.Content(
                        latestEntries = (0..3).map {
                            Diary(
                                id = it.toLong(),
                                entry = "<strong>Awesome</strong> Diary",
                                date = testClock.now(),
                                isFavorite = false,
                                location = Location.Empty,
                            )
                        },
                        totalEntries = 3,
                        weeklySummary = "This is the weekly summary of all the entries",
                        currentStreak = Streak(
                            0,
                            testClock.now().toDate(),
                            testClock.now().toDate(),
                        ),
                        bestStreak = Streak(
                            0,
                            testClock.now().toDate(),
                            testClock.now().toDate(),
                        ),
                        showBiometricAuthDialog = true,
                    ),
                    onAddEntry = {},
                    onSeeAll = {},
                    onToggleFavorite = {},
                    onDiaryClick = {},
                    onDisableBiometricAuth = {},
                    onEnableBiometric = {},
                    onToggleLatestEntries = {},
                    onToggleWeeklySummaryCard = {},
                    onToggleGlanceCard = {},
                )
            }
        }
    }

    @Test
    fun `Dashboard Screen - Hide weekly summary`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme {
                DashboardScreenContent(
                    state = DashboardViewModel.DashboardScreenState.Content(
                        (0..3).map {
                            Diary(
                                id = it.toLong(),
                                entry = "<strong>Awesome</strong> Diary",
                                date = testClock.now(),
                                isFavorite = false,
                                location = Location.Empty,
                            )
                        },
                        3,
                        "This is the weekly summary of all the entries",
                        Streak(
                            0,
                            testClock.now().toDate(),
                            testClock.now().toDate(),
                        ),
                        bestStreak = Streak(
                            0,
                            testClock.now().toDate(),
                            testClock.now().toDate(),
                        ),
                        showWeeklySummary = false,
                    ),
                    onAddEntry = {},
                    onSeeAll = {},
                    onToggleFavorite = {},
                    onDiaryClick = {},
                    onDisableBiometricAuth = {},
                    onEnableBiometric = {},
                    onToggleLatestEntries = {},
                    onToggleWeeklySummaryCard = {},
                    onToggleGlanceCard = {},
                )
            }
        }
    }

    @Test
    fun `Dashboard Screen - Hide latest entries`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme {
                DashboardScreenContent(
                    state = DashboardViewModel.DashboardScreenState.Content(
                        (0..3).map {
                            Diary(
                                id = it.toLong(),
                                entry = "<strong>Awesome</strong> Diary",
                                date = testClock.now(),
                                isFavorite = false,
                                location = Location.Empty,
                            )
                        },
                        3,
                        "This is the weekly summary of all the entries",
                        Streak(
                            0,
                            testClock.now().toDate(),
                            testClock.now().toDate(),
                        ),
                        bestStreak = Streak(
                            0,
                            testClock.now().toDate(),
                            testClock.now().toDate(),
                        ),
                        showLatestEntries = false,
                        showWeeklySummary = true,
                    ),
                    onAddEntry = {},
                    onSeeAll = {},
                    onToggleFavorite = {},

                    onDiaryClick = {},
                    onDisableBiometricAuth = {},
                    onEnableBiometric = {},
                    onToggleLatestEntries = {},
                    onToggleWeeklySummaryCard = {},
                    onToggleGlanceCard = {},
                )
            }
        }
    }
}
