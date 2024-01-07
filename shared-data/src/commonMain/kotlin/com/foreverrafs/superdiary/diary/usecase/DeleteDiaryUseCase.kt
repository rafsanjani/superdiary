package com.foreverrafs.superdiary.diary.usecase

import co.touchlab.kermit.Logger
import com.foreverrafs.superdiary.diary.Result
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import okio.IOException

class DeleteDiaryUseCase(
    private val dataSource: DataSource,
) {
    suspend operator fun invoke(diary: Diary): DiaryListResult {
        return try {
            dataSource.delete(diary)
            Result.Success(data = listOf(diary))
        } catch (exception: IOException) {
            Logger.e(Tag, exception) {
                "Error deleting diary $diary"
            }
            Result.Failure(exception)
        }
    }

    companion object {
        private val Tag = DeleteDiaryUseCase::class.simpleName.orEmpty()
    }
}
