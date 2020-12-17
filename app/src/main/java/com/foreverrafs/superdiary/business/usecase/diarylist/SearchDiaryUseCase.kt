package com.foreverrafs.superdiary.business.usecase.diarylist

import com.foreverrafs.superdiary.business.repository.DiaryRepository

class SearchDiaryUseCase(
    private val repository: DiaryRepository
) {
    suspend operator fun invoke(query: String) {

    }
}