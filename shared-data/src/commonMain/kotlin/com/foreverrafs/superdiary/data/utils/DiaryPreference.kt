package com.foreverrafs.superdiary.data.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.foreverrafs.superdiary.data.getDatastorePath
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toPath

interface DiaryPreference {
    val settings: Flow<DiarySettings>
    val snapshot: DiarySettings
    suspend fun save(settings: DiarySettings)
    suspend fun clear()
}

class DiaryPreferenceImpl(
    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath {
        getDatastorePath(filename = "datastore.preferences_pb").toPath()
    },
) : DiaryPreference {

    private val isFirstLaunchKey = booleanPreferencesKey("isFirstLaunch")
    private val showWeeklySummaryKey = booleanPreferencesKey("showWeeklySummary")
    private val showAtAGlanceKey = booleanPreferencesKey("showAtAGlance")
    private val showLatestEntriesKey = booleanPreferencesKey("showLatestEntries")

    override val settings: Flow<DiarySettings> = dataStore.data.map {
        DiarySettings(
            isFirstLaunch = it[isFirstLaunchKey] ?: true,
            showWeeklySummary = it[showWeeklySummaryKey] ?: true,
            showAtAGlance = it[showAtAGlanceKey] ?: true,
            showLatestEntries = it[showLatestEntriesKey] ?: true,
        )
    }

    /**
     * Obtains a snapshot of the settings at a particular point in time. This is a blocking
     * operation and shouldn't be used in production code
     */
    override val snapshot: DiarySettings
        get() {
            val prefs = runBlocking { dataStore.data.first() }
            return DiarySettings(
                isFirstLaunch = prefs[isFirstLaunchKey] as Boolean,
                showWeeklySummary = prefs[showWeeklySummaryKey] as Boolean,
                showAtAGlance = prefs[showAtAGlanceKey] as Boolean,
                showLatestEntries = prefs[showLatestEntriesKey] as Boolean,
            )
        }

    override suspend fun save(settings: DiarySettings) {
        dataStore.edit {
            it[isFirstLaunchKey] = settings.isFirstLaunch
            it[showWeeklySummaryKey] = settings.showWeeklySummary
            it[showAtAGlanceKey] = settings.showAtAGlance
            it[showLatestEntriesKey] = settings.showLatestEntries
        }
    }

    override suspend fun clear() {
        dataStore.edit {
            it.clear()
        }
    }
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
