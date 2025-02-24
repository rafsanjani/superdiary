package com.foreverrafs.superdiary.domain.repository

import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.WeeklySummary
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

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
    suspend fun add(diary: Diary): Long

    /**
     * Adds all diary items to the datasource. This operation is synchronous
     * and will return after the operation has either succeeded or failed.
     *
     * @return The number of inserted entries
     */
    suspend fun addAll(diaries: List<Diary>): Long

    /**
     * Updates an existing item with the same id with the properties of the new
     * item
     */
    suspend fun update(diary: Diary): Int

    /** Delete multiple diaries */
    suspend fun delete(diaries: List<Diary>): Int

    /**
     * Fetch all the diary items from the datasource, returning a list of all
     * the items that were successfully fetched. The flow returned from this
     * function will publish data changes to subscribers
     *
     * @return a list of diary items that were fetched
     */
    fun fetchAll(): Flow<List<Diary>>

    /**
     * Fetch favorite diary items from the datasource, returning a list of all
     * the items that were successfully fetched. The flow returned from this
     * function will publish data changes and updates to subscribers.
     *
     * @return a list of diary items that were fetched
     */
    fun fetchFavorites(): Flow<List<Diary>>

    /**
     * Search for matching Diaries with entries matching [entry]. This will
     * perform a FTS of the query and return all matching diaries.
     */
    fun find(entry: String): Flow<List<Diary>>

    /** Search for matching diaries for a specific date */
    fun findByDate(date: Instant): Flow<List<Diary>>

    /** Search for diaries between two dates inclusive */
    fun find(from: Instant, to: Instant): Flow<List<Diary>>

    /** Search for a diary by its id */
    fun find(id: Long): Diary?

    /** Deletes all the diary entries from the data source. */
    suspend fun deleteAll()

    /** Obtains the latest [count] entries from the datasource */
    fun getLatestEntries(count: Int): Flow<List<Diary>>

    /** Count all the entries available in the database */
    suspend fun countEntries(): Long

    /** Insert a weekly summary */
    suspend fun insertWeeklySummary(summary: WeeklySummary)

    /** Fetch all weekly summary entries */
    fun getWeeklySummary(): WeeklySummary?

    /** Clear all chat messages from the system */
    suspend fun clearChatMessages()

    suspend fun getPendingDeletes(): List<Diary>
    suspend fun getPendingSyncs(): List<Diary>
}
