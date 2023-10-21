package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.Result
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary

class DeleteMultipleDiariesUseCase(
    private val dataSource: DataSource,
) {
    suspend operator fun invoke(diary: List<Diary>): Result {
        return try {
            dataSource.delete(diary)
            Result.Success(data = diary)
        } catch (exception: Exception) {
            Result.Failure(exception)
        }
    }
}
