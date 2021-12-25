package com.foreverrafs.superdiary.business.repository

import com.foreverrafs.superdiary.business.model.Diary
import kotlinx.coroutines.flow.Flow

interface DataSource {
    suspend fun add(diary: Diary): Long
    suspend fun delete(diary: Diary): Int
    fun getAllDiaries(): Flow<List<Diary>>
    fun searchDiary(query: String): Flow<Diary>
}