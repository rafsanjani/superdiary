package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class LocalStorageDataSource : DataSource {
    private val diaries = mutableListOf<Diary>()

    override suspend fun add(diary: Diary): Long {
        diaries.add(diary)
        return 1
    }

    override suspend fun delete(diary: Diary): Int {
        return if (diaries.remove(diary)) 1 else 0
    }

    override suspend fun fetchAll(): List<Diary> {
        return diaries
    }

    override suspend fun find(query: String): List<Diary> {
        return diaries.filter { it.entry.contains(query, ignoreCase = true) }
    }

    override fun fetchAllAsFlow(): Flow<List<Diary>> {
        return flowOf(diaries)
    }

    override suspend fun deleteAll() {
        diaries.clear()
    }
}