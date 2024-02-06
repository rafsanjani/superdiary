package com.foreverrafs.superdiary.data.usecase

import co.touchlab.kermit.Logger
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.datasource.DataSource
import okio.IOException

class DeleteAllDiariesUseCase(
    private val dataSource: DataSource,
) {
    suspend operator fun invoke(): DiaryListResult {
        return try {
            dataSource.deleteAll()
            Logger.i("Deleted all diary entries")
            Result.Success(data = listOf())
        } catch (exception: IOException) {
            Logger.e(tag = Tag, exception) { "Error deleting all diary entries" }
            Result.Failure(exception)
        }
    }

    companion object {
        private val Tag = DeleteAllDiariesUseCase::class.simpleName.orEmpty()
    }
}
