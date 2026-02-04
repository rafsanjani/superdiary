package com.foreverrafs.superdiary.dashboard.domain

import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.datasource.InitialSyncState
import com.foreverrafs.superdiary.data.datasource.Syncable
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

class GetRecentEntriesUseCase(
    private val dataSource: DataSource,
    private val logger: AggregateLogger,
) {
    suspend operator fun invoke(count: Int): Result<List<Diary>> = try {
        require(dataSource is Syncable) {
            "Datasource should implement Syncable interface"
        }

        Result.Success(
            data = withTimeout(11_000) {
                flow {
                    val syncState = dataSource.initialSyncState.first {
                        it == InitialSyncState.Completed || it == InitialSyncState.Failed
                    }

                    if (syncState == InitialSyncState.Failed) {
                        logger.w(TAG) { "Initial sync failed; continuing with local data." }
                    }

                    logger.i(TAG) { "Sync completed: Fetching latest entries" }
                    val data = dataSource.getLatest(count).first()
                    emit(data)
                }.first { it.isNotEmpty() }
            },
        )
    } catch (e: Exception) {
        logger.e(TAG, e) {
            "Error fetching latest entries"
        }
        Result.Failure(e)
    }

    companion object {
        private const val TAG = "GetRecentEntriesUseCase"
    }
}
