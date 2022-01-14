package com.foreverrafs.domain.feature_diary.repository

import com.foreverrafs.domain.feature_diary.Result
import com.foreverrafs.domain.feature_diary.model.Diary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class RepositoryImpl(
    private val dataSource: DataSource,
) : Repository {
    private val dispatcher = Dispatchers.IO

    override suspend fun add(diary: Diary): Result<Long> = withContext(dispatcher) {
        try {
            val result = dataSource.add(diary)
            Result.Success(result)
        } catch (ex: Exception) {
            Result.Error(ex)
        }
    }

    override suspend fun delete(diary: Diary): Result<Int> = withContext(dispatcher) {
        try {
            Result.Success(data = dataSource.delete(diary))
        } catch (ex: Exception) {
            Result.Error(ex)
        }
    }

    override suspend fun getAllDiaries(): Flow<Result<List<Diary>>> = flow {
        withContext(dispatcher) {
            dataSource
                .fetchAll()
                .catch { error ->
                    emit(Result.Error(error))
                }
                .flowOn(Dispatchers.Main)
                .collect {
                    emit(Result.Success(data = it))
                }
        }
    }

    override suspend fun searchDiary(title: String): Flow<Result<List<Diary>>> {
        TODO("Not yet implemented")
    }
}