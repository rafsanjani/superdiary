package com.foreverrafs.superdiary.business.usecase.add

import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.business.repository.DataSource

class AddDiaryUseCase(private val repository: DataSource) {
    suspend operator fun invoke(diary: Diary) = repository.add(diary)
}