package com.foreverrafs.superdiary.usecase

import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

@Deprecated("Please reconsider why you might want to use this instead of a feature specific usecase")
class GetAllDiariesUseCase(
    private val dataSource: DataSource,
    private val dispatchers: AppCoroutineDispatchers,
) {
    operator fun invoke(): Flow<Result<List<Diary>>> =
        dataSource.fetchAll().map { Result.Success(it) }.flowOn(dispatchers.io)
}
