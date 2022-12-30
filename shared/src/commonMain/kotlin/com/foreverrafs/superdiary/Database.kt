package com.foreverrafs.superdiary

import com.foreverrafs.superdiary.diary.model.Diary
import db.DatabaseQueries
import db.KmpSuperDiaryDB

class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = KmpSuperDiaryDB(databaseDriverFactory.createDriver())
    private val queries = database.databaseQueries

    private val diaryMapper = { id: Long, entry: String, date: String ->
        Diary(id, entry, date)
    }

    fun addDiary(diary: Diary) = queries.insert(diary.entry, diary.date)

    fun deleteDiary(id: Long) = queries.delete(id)

    fun getAllDiaries(): List<Diary> = queries.selectAll(
        mapper = diaryMapper
    ).executeAsList()

    fun findDiary(query: String): List<Diary> = queries.find(entry = query, mapper = diaryMapper)
        .executeAsList()

    fun clearDiaries() = queries.deleteAll()

    fun getDatabaseQueries(): DatabaseQueries = queries

}