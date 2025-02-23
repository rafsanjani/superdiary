package com.foreverrafs.superdiary.domain.usecase

import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource

class GetDiaryByIdUseCase(
    private val dataSource: DataSource,
) {
    operator fun invoke(id: Long): Diary? = dataSource.find(id)
}
