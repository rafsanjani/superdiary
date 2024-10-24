package com.foreverrafs.superdiary.data.utils

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class DiaryPreferenceTest {

    private val diaryPreference: DiaryPreference = DiaryPreferenceImpl.getInstance("superdiary.preferences_pb")

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should update preferences when settings is changed`() = runTest {
        val initialSettings = DiarySettings(
            isFirstLaunch = true,
            showWeeklySummary = true,
            showAtAGlance = true,
            showLatestEntries = true,
            showLocationPermissionDialog = false,
        )

        diaryPreference.save(
            settings = initialSettings,
        )

        // The settings flow is not emitting immediately after a call to save
        // in testing. Use the snapshot to get the latest value from it and test that
        // instead.
        val initialState = diaryPreference.snapshot
        assertThat(initialState).isEqualTo(initialSettings)

        val updatedSettings = DiarySettings(
            isFirstLaunch = false,
            showWeeklySummary = false,
            showAtAGlance = false,
            showLatestEntries = true,
            showLocationPermissionDialog = false,
        )

        diaryPreference.save(updatedSettings)
        val finalState = diaryPreference.snapshot
        assertThat(finalState).isEqualTo(updatedSettings)
    }

    @Test
    fun `Should return the same instance of diary preference`() = runTest {
        val first = DiaryPreferenceImpl.getInstance()
        val second = DiaryPreferenceImpl.getInstance()

        assertThat(first).isEqualTo(second)
    }
}
