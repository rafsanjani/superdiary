package com.foreverrafs.superdiary.business.usecase.diarylist

import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.business.repository.DataSource
import kotlinx.coroutines.flow.Flow

class GetAllDiariesUseCase(private val repo: DataSource) {
    operator fun invoke(): Flow<List<Diary>> {
        return repo.getAllDiaries()
    }
}