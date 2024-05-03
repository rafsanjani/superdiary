package com.foreverrafs.superdiary.data.utils

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.hasClass
import assertk.assertions.isEqualTo
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okio.Path.Companion.toPath

@OptIn(ExperimentalCoroutinesApi::class)
class DiaryPreferenceTest {

    private val filename: String = "${Random.nextInt()}.preferences_pb"
    private val diaryPreference: DiaryPreference = DiaryPreferenceImpl.getInstance(filename)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterTest
    fun teardown() {
        fileSystem.delete(filename.toPath(), true)
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

    @Test
    fun `Should reset settings when clear function is invoked`() = runTest {
        val initialSettings = DiarySettings(
            isFirstLaunch = true,
            showWeeklySummary = true,
            showAtAGlance = true,
            showLatestEntries = true,
        )

        diaryPreference.save(
            settings = initialSettings,
        )

        diaryPreference.clear()

        assertFailure {
            diaryPreference.snapshot
        }.hasClass(IllegalStateException::class)
    }
}
