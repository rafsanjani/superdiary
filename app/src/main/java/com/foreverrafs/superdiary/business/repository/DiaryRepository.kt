package com.foreverrafs.superdiary.business.repository

import com.foreverrafs.superdiary.business.model.Diary

class DiaryRepository(private val dataSource: DataSource) : DataSource {
    override suspend fun add(diary: Diary): Long = dataSource.add(diary)
    override suspend fun delete(diary: Diary): Int = dataSource.delete(diary)
    override fun getAllDiaries() = dataSource.getAllDiaries()
}