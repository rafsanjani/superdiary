package com.foreverrafs.superdiary.domain.usecase

import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class GetDiaryByIdUseCase(
    private val dispatchers: AppCoroutineDispatchers,
    private val dataSource: DataSource,
) {
    operator fun invoke(id: Long): Flow<Diary?> = dataSource.find(id).flowOn(dispatchers.io)
}
