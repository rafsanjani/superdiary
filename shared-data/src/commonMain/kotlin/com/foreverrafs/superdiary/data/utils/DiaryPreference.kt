package com.foreverrafs.superdiary.data.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath

abstract class DiaryPreference {
    private val dataStore: DataStore<Preferences> =
        PreferenceDataStoreFactory.createWithPath {
            getDataStorePath(filename = "datastore.preferences_pb").toPath()
        }

    private val isFirstLaunchKey = booleanPreferencesKey("isFirstLaunch")
    private val showWeeklySummaryKey = booleanPreferencesKey("showWeeklySummary")
    private val showAtAGlanceKey = booleanPreferencesKey("showAtAGlance")
    private val showLatestEntriesKey = booleanPreferencesKey("showLatestEntries")

    val settings: Flow<DiarySettings> = dataStore.data.map {
        DiarySettings(
            isFirstLaunch = it[isFirstLaunchKey] ?: true,
            showWeeklySummary = it[showWeeklySummaryKey] ?: true,
            showAtAGlance = it[showAtAGlanceKey] ?: true,
            showLatestEntries = it[showLatestEntriesKey] ?: true,
        )
    }

    suspend fun save(settings: DiarySettings) {
        dataStore.edit {
            it[isFirstLaunchKey] = settings.isFirstLaunch
            it[showWeeklySummaryKey] = settings.showWeeklySummary
            it[showAtAGlanceKey] = settings.showAtAGlance
            it[showLatestEntriesKey] = settings.showLatestEntries
        }
    }

    abstract fun getDataStorePath(filename: String): String
}

data class DiarySettings(
    val isFirstLaunch: Boolean,
    val showWeeklySummary: Boolean,
    val showAtAGlance: Boolean,
    val showLatestEntries: Boolean,
) {

    companion object {
        val Empty: DiarySettings = DiarySettings(
            isFirstLaunch = false,
            showWeeklySummary = true,
            showAtAGlance = true,
            showLatestEntries = true,
        )
    }
}
