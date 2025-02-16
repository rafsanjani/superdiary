package com.foreverrafs.superdiary.core.sync

import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.data.datasource.remote.DiaryApi
import com.foreverrafs.superdiary.data.model.toDiary
import kotlinx.coroutines.withContext

interface Synchronizer {
    suspend fun sync()
}

class DiarySynchronizer(
    private val diaryApi: DiaryApi,
    private val database: LocalDataSource,
    private val logger: AggregateLogger,
    private val appCoroutineDispatchers: AppCoroutineDispatchers,
) : Synchronizer {
    override suspend fun sync() {
        logger.d(TAG) {
            "Starting sync operation"
        }
        withContext(appCoroutineDispatchers.io) {
            // fetch all entries from remote
            diaryApi.fetchAll().collect { diaryDtoList ->
                // insert remote entries into database. This will set isSynced flag
                // to true and trigger an update for observers
                val savedEntries = database.addAll(diaryDtoList.map { it.toDiary() })

                logger.d(TAG) {
                    "Saved $savedEntries new items to local database"
                }
            }
        }
    }

    companion object {
        private const val TAG = "DiarySynchronizer"
    }
}
