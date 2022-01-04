package com.foreverrafs.superdiary.business.repository

import com.foreverrafs.superdiary.business.model.Diary
import kotlinx.coroutines.flow.Flow

/**
 * The DataSource represents the lowest level of data retrieval in the appliaction architecture.
 * Implementations of the datasource do not care about any exceptions and leave this to the Repository
 * layer instead.
 */
interface DataSource {
    suspend fun add(diary: Diary): Long
    suspend fun delete(diary: Diary): Int
    fun getAllDiaries(): Flow<List<Diary>>
    fun searchDiary(query: String): Flow<Diary>
}