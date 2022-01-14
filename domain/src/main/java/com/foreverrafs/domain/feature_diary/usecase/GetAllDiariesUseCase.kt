package com.foreverrafs.domain.feature_diary.usecase

import com.foreverrafs.domain.feature_diary.repository.Repository

class GetAllDiariesUseCase(private val dataSource: Repository) {
    suspend operator fun invoke() = dataSource.getAllDiaries()
}