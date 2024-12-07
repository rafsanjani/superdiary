package com.foreverrafs.superdiary.domain.usecase

import com.foreverrafs.superdiary.core.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import kotlinx.coroutines.withContext
import okio.IOException

class DeleteDiaryUseCase(
    private val dataSource: DataSource,
    private val dispatchers: AppCoroutineDispatchers,
) {
    suspend operator fun invoke(diaries: List<Diary>): Result<Int> = withContext(dispatchers.io) {
        try {
            Result.Success(dataSource.delete(diaries))
        } catch (exception: IOException) {
            Result.Failure(exception)
        }
    }
}
