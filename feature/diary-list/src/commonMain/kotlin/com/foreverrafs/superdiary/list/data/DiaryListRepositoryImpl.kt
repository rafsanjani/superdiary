package com.foreverrafs.superdiary.list.data

import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.datasource.OfflineFirstDataSource
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.toDatabase
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.list.domain.repository.DiaryListRepository
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow

class DiaryListRepositoryImpl(
    private val dataSource: DataSource,
) : DiaryListRepository {

    override fun getDiaryById(id: Long): Diary? = dataSource.find(id)

    override fun getAllDiaries(): Flow<List<Diary>> = dataSource.fetchAll()

    override suspend fun deleteDiaries(diaries: List<Diary>): Result<Int> = try {
        val result = dataSource.delete(diaries)
        Result.Success(result)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Result.Failure(e)
    }

    override suspend fun updateDiary(diary: Diary): Result<Boolean> = try {
        dataSource.update(diary)
        Result.Success(true)
    } catch (e: Exception) {
        Result.Failure(e)
    }
}
