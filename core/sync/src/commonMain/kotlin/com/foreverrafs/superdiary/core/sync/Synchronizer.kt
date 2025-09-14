package com.foreverrafs.superdiary.core.sync

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.LifecycleStartEffect
import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.datasource.remote.DiaryApi
import com.foreverrafs.superdiary.data.model.toDiary
import com.foreverrafs.superdiary.domain.Synchronizer
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.toDto
import com.foreverrafs.superdiary.domain.repository.DataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiarySynchronizer(
    private val diaryApi: DiaryApi,
    private val dataSource: DataSource,
    private val logger: AggregateLogger,
    private val appCoroutineDispatchers: AppCoroutineDispatchers,
) : Synchronizer {
    private val coroutineScope = CoroutineScope(appCoroutineDispatchers.io)
    private var diaryApiListeningJob: Job? = null

    override suspend fun startListening() {
        logger.i(TAG) {
            "Listening for remote database changes!"
        }

        sync()

        diaryApiListeningJob = coroutineScope.launch(appCoroutineDispatchers.main) {
            // fetch all entries from remote
            diaryApi.fetchAll().collect { diaryDtoList ->
                // Delete all entries from the database
                dataSource.deleteAll()

                // insert remote entries into database. This will set isSynced flag
                // to true and trigger an update for observers
                val savedEntries = dataSource.save(diaryDtoList.map { it.toDiary() })

                logger.i(TAG) {
                    "Saved $savedEntries new items to local database"
                }
            }
        }
    }

    private suspend fun sync() = withContext(appCoroutineDispatchers.io) {
        logger.i(TAG) {
            "Syncing all previously out of sync data to the network"
        }

        val pendingSyncs = dataSource.getPendingDeletes() + dataSource.getPendingSyncs()

        if (pendingSyncs.isEmpty()) {
            logger.i(TAG) {
                "No entry marked for deletion or save."
            }
        }

        logger.i(TAG) {
            "Syncing ${pendingSyncs.size} previously modified entries to network"
        }

        pendingSyncs.forEach { entry ->
            launch {
                if (!entry.isSynced) performSaveSync(diary = entry)
                if (entry.isMarkedForDelete) {
                    performDeleteSync(diary = entry)
                }
            }
        }
    }

    override suspend fun sync(operation: Synchronizer.SyncOperation): Boolean = when (operation) {
        is Synchronizer.SyncOperation.Save -> performSaveSync(operation.diary)
        is Synchronizer.SyncOperation.Delete -> performDeleteSync(operation.diary)
    }

    private suspend fun performSaveSync(diary: Diary): Boolean {
        // Attempt to write the saved diary into the network
        logger.i(TAG) {
            "Writing saved entry onto the network"
        }

        return when (val result = diaryApi.save(diary.toDto())) {
            is Result.Success -> {
                logger.i(TAG) {
                    "Successfully synced saved diary with network"
                }

                // Remove the sync flag from entry in the local database
                dataSource.update(
                    diary = diary.copy(isSynced = true),
                )

                true
            }

            is Result.Failure -> {
                logger.i(TAG) {
                    "There was an error syncing saved entry to the network. ${result.error}"
                }

                false
            }
        }
    }

    private suspend fun performDeleteSync(diary: Diary): Boolean {
        // Attempt to delete the deleted diary from the network
        return when (val result = diaryApi.delete(diary.toDto())) {
            is Result.Success -> {
                logger.i(TAG) {
                    "Successfully synced deleted diary with network"
                }

                // Delete entry from database
                val count = dataSource.delete(
                    listOf(diary),
                )

                logger.i(TAG) {
                    "$count entries synced and deleted from local"
                }
                true
            }

            is Result.Failure -> {
                logger.i(TAG) {
                    "There was an error syncing deleted entry to the network. ${result.error}"
                }

                false
            }
        }
    }

    override fun stopListening() {
        logger.i(TAG) {
            "Stopped listening for remote database changes!"
        }

        diaryApiListeningJob?.cancel(
            CancellationException("Screen moved to paused state!"),
        )
        diaryApiListeningJob = null
    }

    companion object {
        private const val TAG = "DiarySynchronizer"
    }
}

@Composable
fun SyncEffect(
    synchronizer: Synchronizer,
    key1: Any? = null,
) {
    val scope = rememberCoroutineScope()

    LifecycleStartEffect(key1) {
        scope.launch {
            synchronizer.startListening()
        }
        onStopOrDispose {
            synchronizer.stopListening()
        }
    }
}
