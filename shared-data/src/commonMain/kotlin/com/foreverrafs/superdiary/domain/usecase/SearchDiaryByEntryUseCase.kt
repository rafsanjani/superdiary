package com.foreverrafs.superdiary.domain.usecase

import androidx.paging.PagingData
import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class SearchDiaryByEntryUseCase(
    private val dataSource: DataSource,
    private val dispatchers: AppCoroutineDispatchers,
) {
    operator fun invoke(entry: String): Flow<PagingData<Diary>> = dataSource.findPaged(entry).flowOn(dispatchers.io)
}
