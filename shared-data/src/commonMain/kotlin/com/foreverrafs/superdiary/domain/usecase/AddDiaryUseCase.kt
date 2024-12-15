package com.foreverrafs.superdiary.domain.usecase

import com.foreverrafs.superdiary.core.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.domain.validator.DiaryValidator
import kotlinx.coroutines.withContext

class AddDiaryUseCase(
    private val dataSource: DataSource,
    private val dispatchers: AppCoroutineDispatchers,
    private val validator: DiaryValidator,
) {
    suspend operator fun invoke(diary: Diary): DiaryListResult = withContext(dispatchers.io) {
        try {
            validator.validate(diary)

            dataSource.add(diary)
            Result.Success(data = listOf(diary))
        } catch (ex: IllegalArgumentException) {
            Result.Failure(ex)
        } catch (ex: Exception) {
            Result.Failure(ex)
        }
    }
}
