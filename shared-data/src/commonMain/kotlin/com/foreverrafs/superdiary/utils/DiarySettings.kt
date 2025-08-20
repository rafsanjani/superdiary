package com.foreverrafs.superdiary.utils

import com.foreverrafs.preferences.Preference
import com.foreverrafs.preferences.PreferenceKey

@Preference("DiaryPreference")
data class DiarySettings(
    @PreferenceKey.Boolean(default = true)
    val isFirstLaunch: Boolean,

    @PreferenceKey.Boolean(default = true)
    val showWeeklySummary: Boolean,

    @PreferenceKey.Boolean(default = true)
    val showAtAGlance: Boolean,

    @PreferenceKey.Boolean(default = true)
    val showLatestEntries: Boolean,

    @PreferenceKey.Boolean(default = false)
    val isBiometricAuthEnabled: Boolean,

    @PreferenceKey.Boolean(default = true)
    val showLocationPermissionDialog: Boolean,

    @PreferenceKey.Boolean(default = true)
    val showBiometricAuthDialog: Boolean,

    @PreferenceKey.Boolean(default = true)
    val dailyReminderEmail: Boolean,
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
            dailyReminderEmail = true,
        )
    }
}
