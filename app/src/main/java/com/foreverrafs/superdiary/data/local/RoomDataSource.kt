package com.foreverrafs.superdiary.data.local

import com.foreverrafs.domain.feature_diary.model.Diary
import com.foreverrafs.domain.feature_diary.repository.DataSource
import com.foreverrafs.superdiary.data.local.database.DiaryDao
import com.foreverrafs.superdiary.data.local.mapper.DiaryMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomDataSource(
    private val diaryDao: DiaryDao,
    private val mapper: DiaryMapper
) : DataSource {
    override suspend fun add(diary: Diary): Long = diaryDao.add(mapper.mapToEntity(diary))

    override suspend fun delete(diary: Diary): Int = diaryDao.delete(mapper.mapToEntity(diary))

    override suspend fun fetchAll(): Flow<List<Diary>> =
        diaryDao.getAllDiaries().map { mapper.mapToDomain(it) }
}