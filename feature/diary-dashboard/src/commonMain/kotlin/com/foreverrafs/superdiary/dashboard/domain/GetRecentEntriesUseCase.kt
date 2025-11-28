package com.foreverrafs.superdiary.dashboard.domain

import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.datasource.remote.DiaryApi
import com.foreverrafs.superdiary.data.model.toDiary
import com.foreverrafs.superdiary.domain.model.Diary

class GetRecentEntriesUseCase(
    private val diaryApi: DiaryApi,
    private val logger: AggregateLogger,
) {
    suspend operator fun invoke(count: Int): Result<List<Diary>> {
        return when (val result = diaryApi.fetch(count = count)) {
            is Result.Failure -> {
                logger.e(TAG, result.error) {
                    "Error fetching recent entries"
                }
                Result.Failure(result.error)
            }

            is Result.Success -> {
                logger.i(TAG) {
                    "Successfully fetched recent entries: ${result.data}"
                }

                Result.Success(result.data.map { it.toDiary() })
            }
        }
    }

    companion object {
        private const val TAG = "GetRecentEntriesUseCase"

    }
}
