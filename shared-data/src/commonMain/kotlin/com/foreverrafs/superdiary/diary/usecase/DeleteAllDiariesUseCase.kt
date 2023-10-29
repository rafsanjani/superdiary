package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.Result
import com.foreverrafs.superdiary.diary.datasource.DataSource

class DeleteAllDiariesUseCase(
    private val dataSource: DataSource,
) {
    suspend operator fun invoke(): DiaryListResult {
        return try {
            dataSource.deleteAll()
            Result.Success(data = listOf())
        } catch (exception: Exception) {
            Result.Failure(exception)
        }
    }
}
