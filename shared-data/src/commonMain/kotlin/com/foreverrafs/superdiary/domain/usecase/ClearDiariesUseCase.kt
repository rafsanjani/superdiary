package com.foreverrafs.superdiary.domain.usecase

import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.domain.repository.DataSource
import kotlinx.coroutines.withContext

class ClearDiariesUseCase(
    private val dataSource: DataSource,
    private val dispatchers: AppCoroutineDispatchers,
) {
    suspend operator fun invoke(): Result<Unit> = withContext(dispatchers.io) {
        try {
            Result.Success(dataSource.deleteAll())
        } catch (exception: Exception) {
            Result.Failure(exception)
        }
    }
}
