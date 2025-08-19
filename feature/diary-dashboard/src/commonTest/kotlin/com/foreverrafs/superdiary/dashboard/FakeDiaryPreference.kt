package com.foreverrafs.superdiary.dashboard

import com.foreverrafs.preferences.DiaryPreference
import com.foreverrafs.superdiary.utils.DiarySettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class FakeDiaryPreference : DiaryPreference {
    var isSaveCalled = false
    var isClearCalled = false

    var settingsResult = DiarySettings(
        isFirstLaunch = true,
        showWeeklySummary = false,
        showAtAGlance = false,
        showLatestEntries = false,
        isBiometricAuthEnabled = false,
        showLocationPermissionDialog = false,
        showBiometricAuthDialog = false,
        dailyReminderEmail = true,
    )

    override val settings: Flow<DiarySettings>
        get() = flowOf(settingsResult)

    override suspend fun save(block: (DiarySettings) -> DiarySettings) {
        isSaveCalled = true
        settingsResult = block(settingsResult)
    }

    override suspend fun getSnapshot(): DiarySettings = settingsResult

    override suspend fun clear() {
        isClearCalled = true
    }
}
