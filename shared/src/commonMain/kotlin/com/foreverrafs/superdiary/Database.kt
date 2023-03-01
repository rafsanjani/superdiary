package com.foreverrafs.superdiary

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.foreverrafs.superdiary.diary.model.Diary
import db.KmpSuperDiaryDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class Database(databaseDriver: DatabaseDriver) {
    private val superDiaryDatabase = KmpSuperDiaryDB(databaseDriver.createDriver())
    private val queries = superDiaryDatabase.databaseQueries

    private val diaryMapper = { id: Long, entry: String, date: String ->
        Diary(id, entry, date)
    }

    fun addDiary(diary: Diary) = queries.insert(diary.entry, diary.date)

    fun deleteDiary(id: Long) = queries.delete(id)

    fun getAllDiaries(): Flow<List<Diary>> = queries.selectAll(
        mapper = diaryMapper,
    )
        .asFlow()
        .mapToList(Dispatchers.Main)

    fun findDiary(query: String): List<Diary> = queries.find(entry = query, mapper = diaryMapper)
        .executeAsList()

    fun clearDiaries() = queries.deleteAll()
}
