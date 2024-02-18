package com.foreverrafs.superdiary.data.usecase

import com.foreverrafs.superdiary.core.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.model.Diary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class SearchDiaryByEntryUseCase(
    private val dataSource: DataSource,
    private val dispatchers: AppCoroutineDispatchers,
) {
    operator fun invoke(entry: String): Flow<List<Diary>> = dataSource.find(entry).flowOn(dispatchers.io)
}
