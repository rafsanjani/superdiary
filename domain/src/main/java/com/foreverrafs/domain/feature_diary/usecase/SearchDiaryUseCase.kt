package com.foreverrafs.domain.feature_diary.usecase

import com.foreverrafs.domain.feature_diary.repository.Repository

class SearchDiaryUseCase(
    private val repository: Repository
) {
    operator fun invoke(query: String) = repository.searchDiary(query)
}