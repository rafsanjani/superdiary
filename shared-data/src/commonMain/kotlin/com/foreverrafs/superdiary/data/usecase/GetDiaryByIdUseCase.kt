package com.foreverrafs.superdiary.data.usecase

import com.foreverrafs.superdiary.core.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.model.Diary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class GetDiaryByIdUseCase(
    private val dispatchers: AppCoroutineDispatchers,
    private val dataSource: DataSource,
) {
    operator fun invoke(id: Long): Flow<Diary?> = dataSource.find(id).flowOn(dispatchers.io)
}
