package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.Result
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.utils.DiaryValidator

class AddDiaryUseCase(
    private val dataSource: DataSource,
    private val validator: DiaryValidator,
) {
    suspend operator fun invoke(diary: Diary): Result {
        return try {
            validator.validate(diary)

            dataSource.add(diary)
            Result.Success(data = listOf(diary))
        } catch (ex: IllegalArgumentException) {
            Result.Failure(ex)
        }
    }
}
