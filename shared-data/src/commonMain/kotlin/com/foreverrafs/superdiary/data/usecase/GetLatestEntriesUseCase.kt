package com.foreverrafs.superdiary.data.usecase

import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.model.Diary
import kotlinx.coroutines.flow.Flow

class GetLatestEntriesUseCase(
    private val dataSource: DataSource,
) {
    operator fun invoke(count: Int): Flow<List<Diary>> = dataSource.getLatestEntries(count)
}
