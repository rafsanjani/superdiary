package com.foreverrafs.domain.feature_diary.repository

import com.foreverrafs.domain.feature_diary.Result
import com.foreverrafs.domain.feature_diary.model.Diary
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun add(diary: Diary): Result<Long>
    suspend fun delete(diary: Diary): Result<Int>
    fun getAllDiaries(): Flow<Result<List<Diary>>>
    fun searchDiary(query: String): Flow<Result<Diary>>
}