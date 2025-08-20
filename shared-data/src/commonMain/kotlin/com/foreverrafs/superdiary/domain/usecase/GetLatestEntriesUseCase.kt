package com.foreverrafs.superdiary.domain.usecase

import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import kotlinx.coroutines.flow.Flow

class GetLatestEntriesUseCase(
    private val dataSource: DataSource,
) {
    operator fun invoke(count: Int): Flow<List<Diary>> = dataSource.getLatestEntries(count)
}
