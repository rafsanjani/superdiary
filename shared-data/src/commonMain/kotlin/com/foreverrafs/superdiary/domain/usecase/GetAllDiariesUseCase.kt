package com.foreverrafs.superdiary.domain.usecase

import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class GetAllDiariesUseCase(
    private val dataSource: DataSource,
    private val dispatchers: AppCoroutineDispatchers,
) {
    operator fun invoke(): Flow<Result<List<Diary>>> = flow {
        emitAll(dataSource.fetchAll())
    }
        .map {
            Result.Success(it) as Result<List<Diary>>
        }
        .catch { e ->
            emit(Result.Failure(e))
        }
        .flowOn(dispatchers.io)
}
