package com.foreverrafs.superdiary.domain.repository

import androidx.paging.PagingData
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.WeeklySummary
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow

/**
 * The DataSource represents the lowest level of data retrieval in the
 * application architecture. Implementations of the datasource do not care
 * about any exceptions and leave this to the Repository layer instead.
 */
interface DataSource {
    /**
     * Add a single diary item to the datasource. This operation is synchronous
     * and will return after the operation has either succeeded or failed.
     *
     * @return 1 if the operation succeeded and 0 otherwise.
     */
    suspend fun save(diary: Diary): Long

    /**
     * Adds all diary items to the datasource. This operation is synchronous
     * and will return after the operation has either succeeded or failed.
     *
     * @return The number of inserted entries
     */
    suspend fun save(diaries: List<Diary>): Long

    /**
     * Updates an existing item with the same id with the properties of the new item
     */
    suspend fun update(diary: Diary): Int

    /** Delete multiple diaries */
    suspend fun delete(diaries: List<Diary>): Int

    /** Fetch diary entries as paged data for long-running entry lists. */
    fun fetchAllPaged(): Flow<PagingData<Diary>>

    /** Fetch favorite diary entries as paged data for long-running entry lists. */
    fun fetchFavoritesPaged(): Flow<PagingData<Diary>>

    /** Search matching diary entries as paged data. */
    fun findPaged(entry: String): Flow<PagingData<Diary>>

    /** Search matching diary entries for a specific date as paged data. */
    fun findByDatePaged(date: Instant): Flow<PagingData<Diary>>

    /** Search matching diary entries between two dates inclusive as paged data. */
    fun findPaged(from: Instant, to: Instant): Flow<PagingData<Diary>>

    /** Search matching diary entries by text within a date range as paged data. */
    fun findPaged(entry: String, from: Instant, to: Instant): Flow<PagingData<Diary>>

    /** Search for a diary by its id */
    fun find(id: Long): Diary?

    /** Deletes all the diary entries from the data source. */
    suspend fun deleteAll()

    /** Obtains the latest [count] entries from the datasource */
    fun getLatest(count: Int): Flow<List<Diary>>

    /** Count all the entries available in the database */
    suspend fun count(): Long

    /** Insert a weekly summary */
    suspend fun save(summary: WeeklySummary)

    /** Fetch all weekly summary entries */
    fun getOne(): WeeklySummary?

    /** Clear all chat messages from the system */
    suspend fun clearChatMessages()
}
