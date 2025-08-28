package com.foreverrafs.superdiary.domain.usecase

import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.domain.NoOpSynchronizer
import com.foreverrafs.superdiary.domain.Synchronizer
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeleteDiaryUseCase(
    private val dataSource: DataSource,
    private val dispatchers: AppCoroutineDispatchers,
    private val synchronizer: Synchronizer = NoOpSynchronizer,
) {
    suspend operator fun invoke(diaries: List<Diary>): Result<Int> = withContext(dispatchers.io) {
        try {
            // mark all the diaries for deletion
            diaries
                .map { diary -> diary.copy(isMarkedForDelete = true) }
                .forEach { dataSource.update(it) }

            diaries.forEach { diary ->
                launch {
                    synchronizer.sync(Synchronizer.SyncOperation.Delete(diary))
                }
            }

            Result.Success(dataSource.delete(diaries))
        } catch (exception: Exception) {
            Result.Failure(exception)
        }
    }
}
