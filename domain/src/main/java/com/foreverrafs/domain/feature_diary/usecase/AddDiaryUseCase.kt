package com.foreverrafs.domain.feature_diary.usecase

import com.foreverrafs.domain.feature_diary.model.Diary
import com.foreverrafs.domain.feature_diary.repository.Repository

class AddDiaryUseCase(private val repository: Repository) {
    suspend operator fun invoke(diary: Diary) = repository.add(diary)
}