package com.foreverrafs.superdiary.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.DataStorePathResolver
import kotlin.concurrent.Volatile
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized

interface DiaryPreference {
    val settings: Flow<DiarySettings>
    suspend fun save(block: (DiarySettings) -> DiarySettings)
    suspend fun getSnapshot(): DiarySettings
    suspend fun clear()
}

class DiaryPreferenceImpl private constructor(
    private val filename: String,
    private val dataStorePathResolver: DataStorePathResolver,
    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath {
        dataStorePathResolver.resolve(filename)
    },
    private val logger: AggregateLogger,
) : DiaryPreference {

    private val isFirstLaunchKey = booleanPreferencesKey("isFirstLaunch")
    private val showWeeklySummaryKey = booleanPreferencesKey("showWeeklySummary")
    private val showAtAGlanceKey = booleanPreferencesKey("showAtAGlance")
    private val showLatestEntriesKey = booleanPreferencesKey("showLatestEntries")
    private val showLocationPermissionDialogKey =
        booleanPreferencesKey("showLocationPermissionDialog")
    private val showBiometricAuthDialogKey = booleanPreferencesKey("showBiometricAuthDialog")
    private val isBiometricAuthEnabledKey = booleanPreferencesKey("isBiometricsAuthEnabled")

    override val settings: Flow<DiarySettings> = dataStore.data.map {
        DiarySettings(
            isFirstLaunch = it[isFirstLaunchKey] ?: true,
            showWeeklySummary = it[showWeeklySummaryKey] ?: true,
            showAtAGlance = it[showAtAGlanceKey] ?: true,
            showLatestEntries = it[showLatestEntriesKey] ?: true,
            showLocationPermissionDialog = it[showLocationPermissionDialogKey] ?: true,
            showBiometricAuthDialog = it[showBiometricAuthDialogKey] ?: true,
            isBiometricAuthEnabled = it[isBiometricAuthEnabledKey] ?: false,
        )
    }

    override suspend fun getSnapshot(): DiarySettings {
        val prefs = dataStore.data.first()

        return DiarySettings(
            isFirstLaunch = prefs[isFirstLaunchKey] ?: false,
            showWeeklySummary = prefs[showWeeklySummaryKey] ?: true,
            showAtAGlance = prefs[showAtAGlanceKey] ?: true,
            showLatestEntries = prefs[showLatestEntriesKey] ?: true,
            showLocationPermissionDialog = prefs[showLocationPermissionDialogKey] ?: false,
            showBiometricAuthDialog = prefs[showBiometricAuthDialogKey] ?: true,
            isBiometricAuthEnabled = prefs[isBiometricAuthEnabledKey] ?: false,
        )
    }

    override suspend fun save(block: (DiarySettings) -> DiarySettings) {
        val currentSettings = getSnapshot()
        val settings = block(currentSettings)

        dataStore.edit {
            it[isFirstLaunchKey] = settings.isFirstLaunch
            it[showWeeklySummaryKey] = settings.showWeeklySummary
            it[showAtAGlanceKey] = settings.showAtAGlance
            it[showLatestEntriesKey] = settings.showLatestEntries
            it[showLocationPermissionDialogKey] = settings.showLocationPermissionDialog
            it[showBiometricAuthDialogKey] = settings.showBiometricAuthDialog
            it[isBiometricAuthEnabledKey] = settings.isBiometricAuthEnabled
        }
        logger.d(Tag) {
            "Settings saved: $settings"
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
            dataStorePathResolver: DataStorePathResolver,
            logger: AggregateLogger,
        ) = synchronized(lock) {
            instance ?: DiaryPreferenceImpl(
                filename = filename,
                dataStorePathResolver = dataStorePathResolver,
                logger = logger,
            ).also { instance = it }
        }

        private val Tag = DiaryPreference::class.simpleName.orEmpty()
    }
}

data class DiarySettings(
    val isFirstLaunch: Boolean,
    val showWeeklySummary: Boolean,
    val showAtAGlance: Boolean,
    val showLatestEntries: Boolean,
    val isBiometricAuthEnabled: Boolean,
    val showLocationPermissionDialog: Boolean,
    val showBiometricAuthDialog: Boolean,
) {
    companion object {
        val Empty: DiarySettings = DiarySettings(
            isFirstLaunch = true,
            showWeeklySummary = true,
            showAtAGlance = true,
            showLatestEntries = true,
            showLocationPermissionDialog = true,
            showBiometricAuthDialog = true,
            isBiometricAuthEnabled = false,
        )
    }
}
