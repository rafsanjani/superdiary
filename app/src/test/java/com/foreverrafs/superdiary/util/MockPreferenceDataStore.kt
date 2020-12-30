package com.foreverrafs.superdiary.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class MockPreferenceDataStore : DataStore<Preferences> {
    override val data: Flow<Preferences>
        get() {
            return emptyFlow()
        }

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        TODO("Stub")
    }

}
