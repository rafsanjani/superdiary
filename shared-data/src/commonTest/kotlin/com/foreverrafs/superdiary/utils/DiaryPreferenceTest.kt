package com.foreverrafs.superdiary.utils

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.DataStorePathResolver
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okio.Path.Companion.toPath

@OptIn(ExperimentalCoroutinesApi::class)
class DiaryPreferenceTest {

    private val dataStorePathResolver: DataStorePathResolver =
        DataStorePathResolver { filename ->
            // The path used here is compatible with all platforms
            "/tmp/Test/TempPath/$filename".toPath()
        }
    private val diaryPreference: DiaryPreference =
        DiaryPreferenceImpl.getInstance(
            filename = "superdiary.preferences_pb",
            dataStorePathResolver = dataStorePathResolver,
            logger = AggregateLogger(emptyList()),
        )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(TestAppDispatchers.main)
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
            showBiometricAuthDialog = false,
            isBiometricAuthEnabled = false,
        )

        diaryPreference.save {
            it.copy(
                isFirstLaunch = initialSettings.isFirstLaunch,
                showWeeklySummary = initialSettings.showWeeklySummary,
                showAtAGlance = initialSettings.showAtAGlance,
                showLatestEntries = initialSettings.showLatestEntries,
                showLocationPermissionDialog = initialSettings.showLocationPermissionDialog,
                showBiometricAuthDialog = initialSettings.showBiometricAuthDialog,
                isBiometricAuthEnabled = initialSettings.isBiometricAuthEnabled,
            )
        }

        // The settings flow is not emitting immediately after a call to save
        // in testing. Use the snapshot to get the latest value from it and test that
        // instead.
        val initialState = diaryPreference.getSnapshot()
        assertThat(initialState).isEqualTo(initialSettings)

        val updatedSettings = DiarySettings(
            isFirstLaunch = false,
            showWeeklySummary = false,
            showAtAGlance = false,
            showLatestEntries = true,
            showLocationPermissionDialog = false,
            isBiometricAuthEnabled = false,
            showBiometricAuthDialog = false,
        )

        diaryPreference.save {
            it.copy(
                isFirstLaunch = updatedSettings.isFirstLaunch,
                showWeeklySummary = updatedSettings.showWeeklySummary,
                showAtAGlance = updatedSettings.showAtAGlance,
                showLatestEntries = updatedSettings.showLatestEntries,
                showLocationPermissionDialog = updatedSettings.showLocationPermissionDialog,
            )
        }
        val finalState = diaryPreference.getSnapshot()
        assertThat(finalState).isEqualTo(updatedSettings)
    }

    @Test
    fun `Should return the same instance of diary preference`() = runTest {
        val first = DiaryPreferenceImpl.getInstance(
            dataStorePathResolver = dataStorePathResolver,
            logger = AggregateLogger(emptyList()),
        )
        val second = DiaryPreferenceImpl.getInstance(
            dataStorePathResolver = dataStorePathResolver,
            logger = AggregateLogger(emptyList()),
        )

        assertThat(first).isEqualTo(second)
    }

    @Test
    fun `Should reset data when preference is cleared`() = runTest {
        diaryPreference.save {
            it.copy(isFirstLaunch = false)
        }

        // Should reset isFirstLaunch back to true
        diaryPreference.clear()

        diaryPreference.settings.test {
            assertThat(awaitItem().isFirstLaunch).isTrue()
        }
    }
}
