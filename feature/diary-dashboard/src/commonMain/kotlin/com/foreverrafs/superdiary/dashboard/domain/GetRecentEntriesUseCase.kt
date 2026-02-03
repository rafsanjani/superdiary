package com.foreverrafs.superdiary.dashboard.domain

import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.datasource.OfflineFirstDataSource
import com.foreverrafs.superdiary.domain.model.Diary
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

class GetRecentEntriesUseCase(
    private val dataSource: OfflineFirstDataSource,
    private val logger: AggregateLogger,
) {
    suspend operator fun invoke(count: Int): Result<List<Diary>> = try {
        Result.Success(
            data = withTimeout(11_000) {
                flow {
                    while (dataSource.isSyncing()) {
                        logger.i(TAG) { "Sync in progress: Waiting for sync to complete!" }
                        delay(1_000)
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
