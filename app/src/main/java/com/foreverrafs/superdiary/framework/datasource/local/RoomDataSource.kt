package com.foreverrafs.superdiary.framework.datasource.local

import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.business.repository.DataSource
import com.foreverrafs.superdiary.framework.datasource.local.database.DiaryDao
import com.foreverrafs.superdiary.framework.datasource.local.mapper.DiaryMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomDataSource(
    private val diaryDao: DiaryDao,
    private val mapper: DiaryMapper
) : DataSource {
    override suspend fun add(diary: Diary): Long {
        return diaryDao.add(mapper.mapToEntity(diary))
    }

    override suspend fun delete(diary: Diary): Int {
        return diaryDao.delete(mapper.mapToEntity(diary))
    }

    override fun getAllDiaries(): Flow<List<Diary>> {
        return diaryDao.getAllDiaries().map { list ->
            list.map {
                mapper.mapToDomain(it)
            }
        }
    }

}