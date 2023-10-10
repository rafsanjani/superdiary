package com.foreverrafs.superdiary.diary

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.foreverrafs.superdiary.database.SuperDiaryDatabase
import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class Database(databaseDriver: DatabaseDriver) {
    private val driver = databaseDriver.createDriver()
    private val superDiaryDatabase = SuperDiaryDatabase(driver)
    private val queries = superDiaryDatabase.databaseQueries

    private val diaryMapper = { id: Long, entry: String, date: String ->
        Diary(id, entry, date)
    }

    /**
     * This is only used on JVM. Schema is created automatically on Android and iOS
     */
    fun createDatabase() {
        SuperDiaryDatabase.Schema.create(driver)
    }

    fun addDiary(diary: Diary) = queries.insert(diary.entry, diary.date)

    fun deleteDiary(id: Long) = queries.delete(id)

    fun getAllDiaries(): Flow<List<Diary>> = queries.selectAll(
        mapper = diaryMapper,
    )
        .asFlow()
        .mapToList(Dispatchers.Main)

    fun findDiaryByEntry(query: String): List<Diary> =
        queries.findByEntry(name = query, mapper = diaryMapper)
            .executeAsList()

    fun findByDate(date: String): List<Diary> = queries.findByDate(
        date = date,
        mapper = diaryMapper,
    ).executeAsList()

    fun findByDateRange(from: String, to: String): List<Diary> =
        queries.findByDateRange(from, to, diaryMapper).executeAsList()

    fun clearDiaries() = queries.deleteAll()
}
