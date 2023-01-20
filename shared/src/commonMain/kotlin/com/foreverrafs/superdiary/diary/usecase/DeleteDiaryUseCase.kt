package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.Result
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary

class DeleteDiaryUseCase(
    private val dataSource: DataSource
) {
    suspend operator fun invoke(diary: Diary): Result {
        return try {
            dataSource.delete(diary)
            Result.Success(data = listOf(diary))
        } catch (ex: Exception) {
            Result.Failure(ex)
        }
    }
}