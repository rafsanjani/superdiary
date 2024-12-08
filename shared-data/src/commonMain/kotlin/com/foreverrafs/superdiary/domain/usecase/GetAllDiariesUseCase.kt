package com.foreverrafs.superdiary.domain.usecase

import com.foreverrafs.superdiary.core.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class GetAllDiariesUseCase(
    private val dataSource: DataSource,
    private val dispatchers: AppCoroutineDispatchers,
) {
    @Suppress("USELESS_CAST")
    operator fun invoke(): Flow<Result<List<Diary>>> = dataSource
        .fetchAll()
        .map {
            Result.Success(it) as Result<List<Diary>>
        }
        .catch {
            emit(Result.Failure(it))
        }
        .flowOn(dispatchers.io)
}
