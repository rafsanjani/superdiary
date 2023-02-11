package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.Result
import com.foreverrafs.superdiary.diary.datasource.DataSource

class SearchDiaryUseCase(
    private val dataSource: DataSource
) {
    suspend operator fun invoke(query: String): Result {
        return try {
            Result.Success(data = dataSource.find(query))
        } catch (exception: Exception) {
            Result.Failure(exception)
        }
    }
}
