package com.foreverrafs.superdiary.business.repository

import com.foreverrafs.superdiary.business.Result
import com.foreverrafs.superdiary.business.model.Diary
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun add(diary: Diary): Result<Long>
    suspend fun delete(diary: Diary): Result<Int>
    fun getAllDiaries(): Flow<Result<List<Diary>>>
    fun searchDiary(query: String): Flow<Result<Diary>>
}