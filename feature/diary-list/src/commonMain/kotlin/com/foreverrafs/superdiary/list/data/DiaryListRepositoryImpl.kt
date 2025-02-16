package com.foreverrafs.superdiary.list.data

import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.mapper.toDiary
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.list.domain.repository.DiaryListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DiaryListRepositoryImpl(
    private val database: Database,
) : DiaryListRepository {

    override fun getDiaryById(id: Long): Diary? = database.findById(id)?.toDiary()

    override fun getAllDiaries(): Flow<List<Diary>> =
        database.getAllDiaries().map { result -> result.map { it.toDiary() } }

    override suspend fun deleteDiaries(diary: List<Diary>): Result<Int> {
        TODO("Not yet implemented")
    }

    override suspend fun updateDiary(diary: Diary): Result<Boolean> {
        TODO("Not yet implemented")
    }

    companion object {
        const val TAG = "DiaryListRepositoryImpl"
    }
}
