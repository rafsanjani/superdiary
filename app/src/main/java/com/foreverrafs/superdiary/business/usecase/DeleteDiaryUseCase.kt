package com.foreverrafs.superdiary.business.usecase

import com.foreverrafs.superdiary.business.Result
import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.business.repository.Repository

class DeleteDiaryUseCase(
    private val dataSource: Repository
) {
    suspend operator fun invoke(diary: Diary): Result<Int> {
        return dataSource.delete(diary)
    }
}