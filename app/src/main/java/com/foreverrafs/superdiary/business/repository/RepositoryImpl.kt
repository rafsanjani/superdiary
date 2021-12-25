package com.foreverrafs.superdiary.business.repository

import com.foreverrafs.superdiary.business.Result
import com.foreverrafs.superdiary.business.model.Diary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class RepositoryImpl(private val dataSource: DataSource) : Repository {

    override suspend fun add(diary: Diary): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val result = dataSource.add(diary)
            Result.Success(result)
        } catch (ex: Exception) {
            Result.Error(ex)
        }
    }

    override suspend fun delete(diary: Diary): Result<Int> = withContext(Dispatchers.IO) {
        try {
            Result.Success(data = dataSource.delete(diary))
        } catch (ex: Exception) {
            Result.Error(ex)
        }
    }

    override fun getAllDiaries(): Flow<Result<List<Diary>>> = flow {
        dataSource
            .getAllDiaries()
            .flowOn(Dispatchers.Main)
            .catch { error ->
                emit(Result.Error(error))
            }
            .collect {
                emit(Result.Success(data = it))
            }
    }

    override fun searchDiary(query: String): Flow<Result<Diary>> = flow {
        dataSource
            .searchDiary(query)
            .flowOn(Dispatchers.Main)
            .collect {
                emit(Result.Success(data = it))
            }
    }
}