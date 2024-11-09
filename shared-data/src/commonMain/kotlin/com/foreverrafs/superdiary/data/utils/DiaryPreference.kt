package com.foreverrafs.superdiary.data.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.foreverrafs.superdiary.data.getDatastorePath
import kotlin.concurrent.Volatile
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toPath

interface DiaryPreference {
    val settings: Flow<DiarySettings>

    @Deprecated(
        "This is a blocking operation and shouldn't be used in production code",
        replaceWith = ReplaceWith("getSnapshot()"),
    )
    val snapshot: DiarySettings
    suspend fun save(settings: DiarySettings)
    suspend fun getSnapshot(): DiarySettings
    suspend fun clear()
}

class DiaryPreferenceImpl private constructor(
    filename: String,
    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath {
        getDatastorePath(filename = filename).toPath()
    },
) : DiaryPreference {

    private val isFirstLaunchKey = booleanPreferencesKey("isFirstLaunch")
    private val showWeeklySummaryKey = booleanPreferencesKey("showWeeklySummary")
    private val showAtAGlanceKey = booleanPreferencesKey("showAtAGlance")
    private val showLatestEntriesKey = booleanPreferencesKey("showLatestEntries")
    private val showLocationPermissionDialogKey =
        booleanPreferencesKey("showLocationPermissionDialog")

    override val settings: Flow<DiarySettings> = dataStore.data.map {
        DiarySettings(
            isFirstLaunch = it[isFirstLaunchKey] ?: true,
            showWeeklySummary = it[showWeeklySummaryKey] ?: true,
            showAtAGlance = it[showAtAGlanceKey] ?: true,
            showLatestEntries = it[showLatestEntriesKey] ?: true,
            showLocationPermissionDialog = it[showLocationPermissionDialogKey] ?: true,
        )
    }

    /**
     * Obtains a snapshot of the settings at a particular point in time. This
     * is a blocking operation and shouldn't be used in production code
     */
    override val snapshot: DiarySettings
        get() {
            val prefs = runBlocking { dataStore.data.first() }

            // Use appropriate defaults when the settings hasn't been set
            return DiarySettings(
                isFirstLaunch = prefs[isFirstLaunchKey] ?: true,
                showWeeklySummary = prefs[showWeeklySummaryKey] ?: true,
                showAtAGlance = prefs[showAtAGlanceKey] ?: true,
                showLatestEntries = prefs[showLatestEntriesKey] ?: true,
                showLocationPermissionDialog = prefs[showLocationPermissionDialogKey] ?: true,
            )
        }

    override suspend fun getSnapshot(): DiarySettings {
        val prefs = dataStore.data.first()

        return DiarySettings(
            isFirstLaunch = prefs[isFirstLaunchKey] ?: true,
            showWeeklySummary = prefs[showWeeklySummaryKey] ?: true,
            showAtAGlance = prefs[showAtAGlanceKey] ?: true,
            showLatestEntries = prefs[showLatestEntriesKey] ?: true,
            showLocationPermissionDialog = prefs[showLocationPermissionDialogKey] ?: true,
        )
    }

    override suspend fun save(settings: DiarySettings) {
        dataStore.edit {
            it[isFirstLaunchKey] = settings.isFirstLaunch
            it[showWeeklySummaryKey] = settings.showWeeklySummary
            it[showAtAGlanceKey] = settings.showAtAGlance
            it[showLatestEntriesKey] = settings.showLatestEntries
            it[showLocationPermissionDialogKey] = settings.showLocationPermissionDialog
        }
    }

    override suspend fun clear() {
        dataStore.edit {
            it.clear()
        }
    }

    @OptIn(InternalCoroutinesApi::class)
    companion object {
        @Volatile
        private var instance: DiaryPreference? = null
        private val lock = SynchronizedObject()

        fun getInstance(
            filename: String = "datastore.preferences_pb",
        ) = synchronized(lock) {
            instance ?: DiaryPreferenceImpl(filename).also { instance = it }
        }
    }
}

data class DiarySettings(
    val isFirstLaunch: Boolean,
    val showWeeklySummary: Boolean,
    val showAtAGlance: Boolean,
    val showLatestEntries: Boolean,
    val showLocationPermissionDialog: Boolean,
) {
    companion object {
        val Empty: DiarySettings = DiarySettings(
            isFirstLaunch = false,
            showWeeklySummary = true,
            showAtAGlance = true,
            showLatestEntries = true,
            showLocationPermissionDialog = true,
        )
    }
}
