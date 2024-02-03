package com.foreverrafs.superdiary.data.usecase

import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.model.Diary
import kotlinx.coroutines.flow.Flow

class SearchDiaryByEntryUseCase(
    private val dataSource: DataSource,
) {
    operator fun invoke(entry: String): Flow<List<Diary>> {
        return dataSource.find(entry)
    }
}
