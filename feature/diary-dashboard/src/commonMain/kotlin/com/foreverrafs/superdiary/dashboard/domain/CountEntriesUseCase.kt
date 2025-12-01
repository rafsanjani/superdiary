package com.foreverrafs.superdiary.dashboard.domain

import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.datasource.remote.DiaryApi

class CountEntriesUseCase(
    private val diaryApi: DiaryApi,
    private val logger: AggregateLogger,
) {
    suspend operator fun invoke(): Result<Long> = when (val result = diaryApi.countItems()) {
        is Result.Failure -> {
            logger.e(TAG, result.error) {
                "Error counting entries"
            }
            Result.Failure(result.error)
        }

        is Result.Success -> {
            logger.i(TAG) {
                "Successfully counted entries: ${result.data}"
            }
            Result.Success(result.data)
        }
    }

    companion object {
        private const val TAG = "CountEntriesUseCase"
    }
}
