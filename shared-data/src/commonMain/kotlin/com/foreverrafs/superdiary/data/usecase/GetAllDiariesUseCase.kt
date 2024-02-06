package com.foreverrafs.superdiary.data.usecase

import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.model.Diary
import kotlinx.coroutines.flow.Flow

class GetAllDiariesUseCase(
    private val dataSource: DataSource,
) {
    operator fun invoke(): Flow<List<Diary>> = dataSource.fetchAll()
}
