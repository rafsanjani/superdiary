package com.foreverrafs.superdiary.data.repository

import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DiaryRepository
import kotlinx.coroutines.flow.Flow

class DiaryRepositoryImpl(
    private val database: Database,
) : DiaryRepository {
    override suspend fun add(diary: Diary): Long {
        TODO("Not yet implemented")
    }

    override suspend fun delete(diary: Diary): Int {
        TODO("Not yet implemented")
    }

    override suspend fun update(diary: Diary): Int {
        TODO("Not yet implemented")
    }

    override fun getAllDiaries(): Flow<List<Diary>> {
        TODO("Not yet implemented")
    }
}
