package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.coroutines.flow.Flow

class GetAllDiariesUseCase(
    dataSource: DataSource,
) {
    val diaries: Flow<List<Diary>> = dataSource.fetchAll()
}
