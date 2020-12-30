package com.foreverrafs.superdiary.business.usecase.common

import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.business.repository.DataSource

class DeleteDiaryUseCase(
    private val repository: DataSource
) {
    suspend operator fun invoke(diary: Diary): Int {
        return repository.delete(diary)
    }
}