package com.foreverrafs.superdiary.business.usecase

import com.foreverrafs.superdiary.business.repository.Repository

class GetAllDiariesUseCase(private val dataSource: Repository) {
    operator fun invoke() = dataSource.getAllDiaries()
}