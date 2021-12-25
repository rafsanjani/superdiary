package com.foreverrafs.superdiary.framework.datasource.local

import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.business.repository.DataSource
import com.foreverrafs.superdiary.framework.datasource.local.database.DiaryDao
import com.foreverrafs.superdiary.framework.datasource.local.mapper.DiaryMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class RoomDataSource(
    private val diaryDao: DiaryDao,
    private val mapper: DiaryMapper
) : DataSource {
    override suspend fun add(diary: Diary): Long = diaryDao.add(mapper.mapToEntity(diary))

    override suspend fun delete(diary: Diary): Int = diaryDao.delete(mapper.mapToEntity(diary))

    override fun searchDiary(query: String): Flow<Diary> =
        diaryDao.searchDiary(query).map { mapper.mapToDomain(it) }

    override fun getAllDiaries(): Flow<List<Diary>> = flow {
        emitAll(diaryDao.getAllDiaries().map { mapper.mapToDomain(it) })
    }
}