package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

internal class LocalStorageDataSource : DataSource {
    private val diaries = mutableListOf<Diary>()

    private val diariesFlow = MutableSharedFlow<List<Diary>>(replay = 1)

    override suspend fun add(diary: Diary): Long = withPublishDiaries {
        diaries.add(diary)
        1L
    }

    override suspend fun delete(diary: Diary): Int = withPublishDiaries {
        if (diaries.remove(diary)) 1 else 0
    }

    override fun fetchAll(): Flow<List<Diary>> {
        return diariesFlow.asSharedFlow()
    }

    override suspend fun find(query: String): List<Diary> {
        return diaries.filter { it.entry.contains(query, ignoreCase = true) }
    }

    override suspend fun deleteAll() = withPublishDiaries {
        withPublishDiaries {
            diaries.clear()
        }
    }

    private fun <T> withPublishDiaries(func: () -> T): T {
        val result = func()

        CoroutineScope(EmptyCoroutineContext).launch {
            diariesFlow.emit(diaries)
        }

        return result
    }
}
