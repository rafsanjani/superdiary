package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.Result
import com.foreverrafs.superdiary.diary.datasource.DataSource

class GetAllDiariesUseCase(
    private val dataSource: DataSource
) {
    suspend operator fun invoke(): Result {
        return try {
            Result.Success(dataSource.fetchAll())
        } catch (exception: Exception) {
            Result.Failure(exception)
        }
    }
}
