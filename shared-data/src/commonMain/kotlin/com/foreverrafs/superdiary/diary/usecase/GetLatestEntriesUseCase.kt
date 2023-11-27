package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.coroutines.flow.Flow

class GetLatestEntriesUseCase(
    private val dataSource: DataSource,
) {
    operator fun invoke(count: Int): Flow<List<Diary>> = dataSource.getLatestEntries(count)
}
