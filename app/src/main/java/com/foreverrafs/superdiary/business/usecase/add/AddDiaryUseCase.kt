package com.foreverrafs.superdiary.business.usecase.add

import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.business.repository.Repository

class AddDiaryUseCase(private val repository: Repository) {
    suspend operator fun invoke(diary: Diary) = repository.add(diary)
}