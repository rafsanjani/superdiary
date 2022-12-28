package com.foreverrafs.superdiary.diary.datasource

import com.foreverrafs.superdiary.LocalDatabaseFactory
import com.foreverrafs.superdiary.diary.model.Diary
import db.KmpSuperDiaryDB

class LocalDataSource private constructor(database: KmpSuperDiaryDB = LocalDatabaseFactory.getSuperDiaryDB()) : DataSource {
    private val queries = database.databaseQueries

    override suspend fun add(diary: Diary): Long {
        queries.insert(diary.entry, diary.date)
        return 1
    }

    override suspend fun delete(diary: Diary): Int {
        diary.id?.let {
            queries.delete(it)
            return 1
        }

        return 0
    }

    override suspend fun fetchAll(): List<Diary> {
        return queries.selectAll(mapper = { id, entry, date -> Diary(id, entry, date) })
            .executeAsList()
    }

    override suspend fun find(query: String): List<Diary> {
        return queries.find(entry = query, mapper = { id, entry, date -> Diary(id, entry, date) })
            .executeAsList()
    }


    override suspend fun deleteAll() {
        return queries.deleteAll()
    }


    companion object {
        fun getInstance(): DataSource = LocalDataSource()
    }
}