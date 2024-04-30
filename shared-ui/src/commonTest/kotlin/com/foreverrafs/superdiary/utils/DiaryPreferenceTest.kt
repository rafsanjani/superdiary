package com.foreverrafs.superdiary.utils

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.data.utils.DiaryPreference
import com.foreverrafs.superdiary.data.utils.DiaryPreferenceImpl
import com.foreverrafs.superdiary.data.utils.DiarySettings
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

    private val  diaryPreference: DiaryPreference =  DiaryPreferenceImpl()

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
        )

        diaryPreference.save(
            settings = initialSettings,
        )

        val initialState = diaryPreference.snapshot
        assertThat(initialState).isEqualTo(initialSettings)

        val updatedSettings = DiarySettings(
            isFirstLaunch = false,
            showWeeklySummary = false,
            showAtAGlance = false,
            showLatestEntries = true,
        )

        diaryPreference.save(updatedSettings)
        val finalState = diaryPreference.snapshot
        assertThat(finalState).isEqualTo(updatedSettings)
    }
}
