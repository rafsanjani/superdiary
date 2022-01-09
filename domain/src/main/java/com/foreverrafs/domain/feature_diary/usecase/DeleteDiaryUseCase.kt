package com.foreverrafs.domain.feature_diary.usecase

import com.foreverrafs.domain.feature_diary.Result
import com.foreverrafs.domain.feature_diary.model.Diary
import com.foreverrafs.domain.feature_diary.repository.Repository

class DeleteDiaryUseCase(
    private val dataSource: Repository
) {
    suspend operator fun invoke(diary: Diary): Result<Int> {
        return dataSource.delete(diary)
    }
}