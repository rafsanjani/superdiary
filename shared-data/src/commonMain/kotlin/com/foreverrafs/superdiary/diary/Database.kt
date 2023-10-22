package com.foreverrafs.superdiary.diary

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.foreverrafs.superdiary.database.SuperDiaryDatabase
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.utils.toDate
import com.foreverrafs.superdiary.diary.utils.toInstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

class Database(databaseDriver: DatabaseDriver) {
    private val driver = databaseDriver.createDriver()
    private val superDiaryDatabase = SuperDiaryDatabase(driver)
    private val queries = superDiaryDatabase.databaseQueries

    private val diaryMapper = { id: Long, entry: String, date: String, favorite: Long ->
        Diary(
            id = id,
            entry = entry,
            date = LocalDate.parse(date).toInstant(),
            isFavorite = favorite.asBoolean(),
        )
    }

    /**
     * This is only used on JVM. Schema is created automatically on Android and iOS
     */
    fun createDatabase() {
        SuperDiaryDatabase.Schema.create(driver)
    }

    fun addDiary(diary: Diary) = queries.insert(diary.entry, diary.date.toDate().toString())

    fun deleteDiary(id: Long) = queries.delete(id)

    fun deleteDiaries(ids: List<Long>): Int = queries.transactionWithResult {
        ids.forEach { id ->
            deleteDiary(id)
        }
        ids.size
    }

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

    fun update(diary: Diary): Int {
        queries.update(
            id = diary.id,
            entry = diary.entry,
            date = diary.date.toDate().toString(),
            favorite = diary.isFavorite.asLong(),
        )

        return queries.getAffectedRows().executeAsOne().toInt()
    }

    fun clearDiaries() = queries.deleteAll()

    private fun Boolean.asLong(): Long = if (this) 1 else 0
    private fun Long.asBoolean(): Boolean = this != 0L
}
