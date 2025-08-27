package com.foreverrafs.superdiary.domain.usecase

import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.domain.NoOpSynchronizer
import com.foreverrafs.superdiary.domain.Synchronizer
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.domain.validator.DiaryValidator
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddDiaryUseCase(
    private val dataSource: DataSource,
    private val dispatchers: AppCoroutineDispatchers,
    private val synchronizer: Synchronizer = NoOpSynchronizer,
    private val validator: DiaryValidator,
) {
    suspend operator fun invoke(diary: Diary): Result<Diary> = withContext(dispatchers.io) {
        try {
            validator.validate(diary)

            // new diary entries are created without ids and let to the database to auto generate them
            // after adding an entry, the generated id is returned from the database
            val diaryId = dataSource.add(diary)

            // Update the diary with the newly generated id and return it

            launch {
                synchronizer.sync(
                    operation = Synchronizer.SyncOperation.Save(
                        diary = diary,
                    ),
                )
            }

            Result.Success(
                data = diary.copy(id = diaryId),
            )
        } catch (ex: Exception) {
            Result.Failure(ex)
        }
    }
}
