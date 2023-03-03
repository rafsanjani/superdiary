package com.foreverrafs.superdiary.diary.datasource

import com.foreverrafs.superdiary.Database
import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.coroutines.flow.Flow

class LocalDataSource(private val database: Database) : DataSource {
    override suspend fun add(diary: Diary): Long {
        database.addDiary(diary)
        return 1
    }

    override suspend fun delete(diary: Diary): Int {
        diary.id?.let {
            database.deleteDiary(it)
            return 1
        }

        return 0
    }

    override fun fetchAll(): Flow<List<Diary>> {
        return database.getAllDiaries()
    }

    override suspend fun find(entry: String): List<Diary> {
        return database.findDiaryByEntry(entry)
    }

    override suspend fun find(from: String, to: String): List<Diary> {
        return database.findByDateRange(from, to)
    }

    override suspend fun findByDate(date: String): List<Diary> {
        return database.findByDate(date)
    }

    override suspend fun deleteAll() {
        return database.clearDiaries()
    }
}
