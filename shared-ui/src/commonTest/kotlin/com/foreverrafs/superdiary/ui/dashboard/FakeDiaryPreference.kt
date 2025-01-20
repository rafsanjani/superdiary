package com.foreverrafs.superdiary.ui.dashboard

import com.foreverrafs.superdiary.utils.DiaryPreference
import com.foreverrafs.superdiary.utils.DiarySettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class FakeDiaryPreference : DiaryPreference {
    var isSaveCalled = false
    var isClearCalled = false

    private var snapshotSettings = DiarySettings(
        isFirstLaunch = true,
        showWeeklySummary = false,
        showAtAGlance = false,
        showLatestEntries = false,
        isBiometricAuthEnabled = false,
        showLocationPermissionDialog = false,
        showBiometricAuthDialog = false,
    )
    override val settings: Flow<DiarySettings>
        get() = flowOf(snapshotSettings)

    override suspend fun save(block: (DiarySettings) -> DiarySettings) {
        isSaveCalled = true
        snapshotSettings = block(snapshotSettings)
    }

    override suspend fun getSnapshot(): DiarySettings = snapshotSettings

    override suspend fun clear() {
        isClearCalled = true
    }
}
