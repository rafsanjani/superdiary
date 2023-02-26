package com.foreverrafs.superdiary.diary.datasource

import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.coroutines.flow.Flow

/**
 * The DataSource represents the lowest level of data retrieval in the application architecture.
 * Implementations of the datasource do not care about any exceptions and leave this to the Repository
 * layer instead.
 */
interface DataSource {
    /**
     * Add a single diary item to the datasource. This operation is
     * synchronous and will return after the operation has either succeeded
     * or failed.
     * @return 1 if the operation succeeded and 0 otherwise.
     */
    suspend fun add(diary: Diary): Long

    /**
     * Deletes the specified diary item from the datasource returning the
     * number of diary items that have been succesfully deleted or 0 otherwise
     */
    suspend fun delete(diary: Diary): Int

    /**
     * Fetch all the diary items from the datasource, returning a list of
     * all the items that were successfully fetched.
     * @return a list of diary items that were fetched
     */
    fun fetchAll(): Flow<List<Diary>>

    /**
     * Search for matching Diaries with entries matching [query]. This
     * will perform a FTS of the query and return all matching diaries.
     */
    suspend fun find(query: String): List<Diary>

    /**
     * Delets all the diary entries from the data source.
     */
    suspend fun deleteAll()
}
