package com.foreverrafs.superdiary.list.data

import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.mapper.toDiary
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.toDatabase
import com.foreverrafs.superdiary.list.domain.repository.DiaryListRepository
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DiaryListRepositoryImpl(
    private val database: Database,
) : DiaryListRepository {

    override fun getDiaryById(id: Long): Diary? = database.findById(id)?.toDiary()

    override fun getAllDiaries(): Flow<List<Diary>> =
        database.getAllDiaries().map { result ->
            result.map { it.toDiary() }
        }

    override suspend fun deleteDiaries(diary: List<Diary>): Result<Int> = try {
        val result = database.deleteDiaries(diary.mapNotNull { it.id })
        Result.Success(result)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Result.Failure(e)
    }

    override suspend fun updateDiary(diary: Diary): Result<Boolean> = try {
        database.update(diary.toDatabase())
        Result.Success(true)
    } catch (e: Exception) {
        Result.Failure(e)
    }
}
