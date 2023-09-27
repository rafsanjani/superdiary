package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.Result
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary

class DeleteDiaryUseCase(
    private val dataSource: DataSource,
) {
    suspend fun deleteDiary(diary: Diary): Result {
        return try {
            dataSource.delete(diary)
            Result.Success(data = listOf(diary))
        } catch (exception: Exception) {
            Result.Failure(exception)
        }
    }

    suspend fun deleteAll(): Result {
        return try {
            dataSource.deleteAll()
            Result.Success(data = listOf())
        } catch (exception: Exception) {
            Result.Failure(exception)
        }
    }
}
