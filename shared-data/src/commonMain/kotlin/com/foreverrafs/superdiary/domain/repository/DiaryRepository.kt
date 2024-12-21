package com.foreverrafs.superdiary.domain.repository

import com.foreverrafs.superdiary.domain.model.Diary
import kotlinx.coroutines.flow.Flow

interface DiaryRepository {
    suspend fun add(diary: Diary): Long

    /**
     * Deletes the specified diary item from the datasource returning the
     * number of diary items that have been successfully deleted or 0 otherwise
     */
    suspend fun delete(diary: Diary): Int

    /**
     * Updates an existing item with the same id with the properties of the new
     * item
     */
    suspend fun update(diary: Diary): Int

    /**
     * Fetch all the diary items from the datasource, returning a list of all
     * the items that were successfully fetched. The flow returned from this
     * function will publish data changes to subscribers
     *
     * @return a list of diary items that were fetched
     */
    fun getAllDiaries(): Flow<List<Diary>>
}
