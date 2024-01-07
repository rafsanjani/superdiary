package com.foreverrafs.superdiary.diary.usecase

import co.touchlab.kermit.Logger
import com.foreverrafs.superdiary.diary.Result
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.validator.DiaryValidator

class AddDiaryUseCase(
    private val dataSource: DataSource,
    private val validator: DiaryValidator,
) {
    suspend operator fun invoke(diary: Diary): DiaryListResult {
        return try {
            validator.validate(diary)

            dataSource.add(diary)
            Logger.i(AddDiaryUseCase::class.simpleName.orEmpty()) {
                "Saved Diary: $diary"
            }
            Result.Success(data = listOf(diary))
        } catch (ex: IllegalArgumentException) {
            Logger.e(tag = Tag, ex) {
                "Error saving diary"
            }
            Result.Failure(ex)
        }
    }

    companion object {
        private val Tag = AddDiaryUseCase::class.simpleName.orEmpty()
    }
}
