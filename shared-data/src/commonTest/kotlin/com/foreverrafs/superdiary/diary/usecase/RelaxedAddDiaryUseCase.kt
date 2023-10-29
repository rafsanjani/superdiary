package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.Result
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary

/**
 * Adds diaries to our local datasource without enforcing any data integrity checks.
 * @see [AddDiaryUseCase] for the original version of this
 */
internal class RelaxedAddDiaryUseCase(private val dataSource: DataSource) {
    suspend operator fun invoke(diary: Diary): DiaryListResult {
        return try {
            dataSource.add(diary)
            Result.Success(data = listOf(diary))
        } catch (ex: Exception) {
            Result.Failure(ex)
        }
    }
}
