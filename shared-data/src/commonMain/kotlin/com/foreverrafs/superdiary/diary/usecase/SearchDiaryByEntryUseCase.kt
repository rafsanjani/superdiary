package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.coroutines.flow.Flow

class SearchDiaryByEntryUseCase(
    private val dataSource: DataSource,
) {
    operator fun invoke(entry: String): Flow<List<Diary>> {
        return dataSource.find(entry)
    }
}
