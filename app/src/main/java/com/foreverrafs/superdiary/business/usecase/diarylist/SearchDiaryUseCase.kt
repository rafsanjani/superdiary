package com.foreverrafs.superdiary.business.usecase.diarylist

import com.foreverrafs.superdiary.business.repository.DataSource

class SearchDiaryUseCase(
    private val repository: DataSource
) {
    suspend operator fun invoke(query: String) {

    }
}