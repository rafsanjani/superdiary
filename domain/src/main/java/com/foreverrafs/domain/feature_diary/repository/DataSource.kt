package com.foreverrafs.domain.feature_diary.repository

import com.foreverrafs.domain.feature_diary.model.Diary
import kotlinx.coroutines.flow.Flow

/**
 * The DataSource represents the lowest level of data retrieval in the appliaction architecture.
 * Implementations of the datasource do not care about any exceptions and leave this to the Repository
 * layer instead.
 */
interface DataSource {
    suspend fun add(diary: Diary): Long
    suspend fun delete(diary: Diary): Int
    suspend fun fetchAll(): Flow<List<Diary>>
}