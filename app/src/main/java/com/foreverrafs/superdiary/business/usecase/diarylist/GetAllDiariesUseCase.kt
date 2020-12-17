package com.foreverrafs.superdiary.business.usecase.diarylist

import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.business.repository.DiaryRepository
import kotlinx.coroutines.flow.Flow

class GetAllDiariesUseCase(private val repo: DiaryRepository) {
    operator fun invoke(): Flow<List<Diary>> {
        return repo.getAllDiaries()
    }
}