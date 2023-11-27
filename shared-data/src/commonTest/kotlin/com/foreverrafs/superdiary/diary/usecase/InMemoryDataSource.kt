package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.utils.toDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlin.coroutines.EmptyCoroutineContext

internal class InMemoryDataSource : DataSource {
    private val diaries = mutableListOf<Diary>()

    private val diariesFlow = MutableSharedFlow<List<Diary>>(replay = 1)

    override suspend fun add(diary: Diary): Long = withPublishDiaries {
        diaries.add(diary)
        1L
    }

    override suspend fun delete(diary: Diary): Int = withPublishDiaries {
        if (diaries.remove(diary)) 1 else 0
    }

    override suspend fun delete(diaries: List<Diary>): Int = withPublishDiaries {
        if (this.diaries.removeAll(diaries)) diaries.size else 0
    }

    override fun fetchAll(): Flow<List<Diary>> {
        return diariesFlow.asSharedFlow()
    }

    override fun fetchFavorites(): Flow<List<Diary>> {
        return diariesFlow.map { diaries ->
            diaries.filter { it.isFavorite }
        }
    }

    override fun find(entry: String): Flow<List<Diary>> {
        return diariesFlow.map { diaries ->
            diaries.filter {
                it.entry.contains(entry, ignoreCase = true)
            }
        }
    }

    override fun find(from: Instant, to: Instant): Flow<List<Diary>> {
        return diariesFlow.map { diaries ->
            diaries.filter {
                val diaryDate = it.date.toDate()

                diaryDate in from.toDate()..to.toDate()
            }
        }
    }

    override fun findByDate(date: Instant): Flow<List<Diary>> {
        return diariesFlow.map { diaries ->
            diaries.filter {
                it.date.toDate() == date.toDate()
            }
        }
    }

    override suspend fun deleteAll() = withPublishDiaries {
        diaries.clear()
    }

    private fun <T> withPublishDiaries(func: () -> T): T {
        val result = func()

        CoroutineScope(EmptyCoroutineContext).launch {
            diariesFlow.emit(diaries)
        }

        return result
    }

    override suspend fun update(diary: Diary) = withPublishDiaries {
        val targetDiary = diaries.firstOrNull { it.id == diary.id } ?: return@withPublishDiaries 0

        diaries[diaries.indexOf(targetDiary)] = targetDiary.copy(
            entry = diary.entry,
            date = diary.date,
            isFavorite = diary.isFavorite,
        )

        return@withPublishDiaries 1
    }

    override fun getLatestEntries(count: Int): Flow<List<Diary>> {
        return diariesFlow.map { diaries ->
            diaries.sortedByDescending { it.date }
        }
            .take(count)
    }
}
