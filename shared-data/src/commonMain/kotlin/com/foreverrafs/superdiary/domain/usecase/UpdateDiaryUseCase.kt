package com.foreverrafs.superdiary.domain.usecase

import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import kotlinx.coroutines.withContext

class UpdateDiaryUseCase(
    private val dataSource: DataSource,
    private val dispatchers: AppCoroutineDispatchers,
) {
    suspend operator fun invoke(diary: Diary): Result<Boolean> = withContext(dispatchers.io) {
        try {
            Result.Success(dataSource.update(diary) != 0)
        } catch (exception: Exception) {
            Result.Failure(exception)
        }
    }
}
