package com.foreverrafs.superdiary.business.usecase.common

import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.business.repository.DiaryRepository

class DeleteDiaryUseCase(
    private val repository: DiaryRepository
) {
    suspend operator fun invoke(diary: Diary): Int {
        return repository.delete(diary)
    }
}