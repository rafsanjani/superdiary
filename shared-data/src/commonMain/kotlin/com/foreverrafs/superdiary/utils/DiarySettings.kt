package com.foreverrafs.superdiary.utils

import com.foreverrafs.preferences.Preference
import com.foreverrafs.preferences.PreferenceKey

@Preference("DiaryPreference")
data class DiarySettings(
    @PreferenceKey(defaultValue = "true")
    val isFirstLaunch: Boolean,

    @PreferenceKey(defaultValue = "true")
    val showWeeklySummary: Boolean,

    @PreferenceKey(defaultValue = "true")
    val showAtAGlance: Boolean,

    @PreferenceKey(defaultValue = "true")
    val showLatestEntries: Boolean,

    @PreferenceKey(defaultValue = "false")
    val isBiometricAuthEnabled: Boolean,

    @PreferenceKey(defaultValue = "true")
    val showLocationPermissionDialog: Boolean,

    @PreferenceKey(defaultValue = "true")
    val showBiometricAuthDialog: Boolean,
) {
    companion object {
        val Empty: DiarySettings = DiarySettings(
            isFirstLaunch = true,
            showWeeklySummary = true,
            showAtAGlance = true,
            showLatestEntries = true,
            isBiometricAuthEnabled = false,
            showLocationPermissionDialog = true,
            showBiometricAuthDialog = true,
        )
    }
}
