package com.foreverrafs.superdiary.business.usecase.diarylist

import com.foreverrafs.superdiary.business.repository.Repository

class SearchDiaryUseCase(
    private val repository: Repository
) {
    operator fun invoke(query: String) = repository.searchDiary(query)
}