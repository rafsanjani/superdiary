package com.foreverrafs.superdiary.diary

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.foreverrafs.superdiary.database.SuperDiaryDatabase
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.utils.toDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

class Database(databaseDriver: DatabaseDriver) {
    private val driver = databaseDriver.createDriver()
    private val superDiaryDatabase = SuperDiaryDatabase(driver)
    private val queries = superDiaryDatabase.databaseQueries

    private val diaryMapper = { id: Long, entry: String, date: String ->
        Diary(id, entry, Instant.parse(date))
    }

    /**
     * This is only used on JVM. Schema is created automatically on Android and iOS
     */
    fun createDatabase() {
        SuperDiaryDatabase.Schema.create(driver)
    }

    fun addDiary(diary: Diary) = queries.insert(diary.entry, diary.date.toString())

    fun deleteDiary(id: Long) = queries.delete(id)

    fun getAllDiaries(): Flow<List<Diary>> = queries.selectAll(
        mapper = diaryMapper,
    )
        .asFlow()
        .mapToList(Dispatchers.Main)

    fun findDiaryByEntry(query: String): Flow<List<Diary>> =
        queries.findByEntry(name = query, mapper = diaryMapper)
            .asFlow()
            .mapToList(Dispatchers.Main)

    fun findByDate(date: Instant): Flow<List<Diary>> = queries.findByDate(
        date = date.toDate().toString(),
        mapper = diaryMapper,
    )
        .asFlow()
        .mapToList(Dispatchers.Main)

    fun findByDateRange(from: Instant, to: Instant): Flow<List<Diary>> =
        queries.findByDateRange(
            from.toDate().toString(),
            to.toDate().toString(),
            diaryMapper,
        )
            .asFlow()
            .mapToList(Dispatchers.Main)

    fun clearDiaries() = queries.deleteAll()
}
