package com.foreverrafs.superdiary.business.usecase.add

import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.business.repository.DiaryRepository

class AddDiaryUseCase(private val repository: DiaryRepository) {
    suspend operator fun invoke(diary: Diary) = repository.add(diary)
}