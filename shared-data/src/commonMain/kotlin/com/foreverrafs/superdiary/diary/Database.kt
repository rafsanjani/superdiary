package com.foreverrafs.superdiary.diary

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.foreverrafs.superdiary.database.SuperDiaryDatabase
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.model.WeeklySummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlin.coroutines.suspendCoroutine

val diaryAdapter = object : ColumnAdapter<Instant, Long> {
    override fun decode(databaseValue: Long): Instant = Instant.fromEpochMilliseconds(databaseValue)

    override fun encode(value: Instant): Long = value.toEpochMilliseconds()
}

class Database(databaseDriver: DatabaseDriver) {
    private val driver = databaseDriver.createDriver()
    private val superDiaryDatabase =
        SuperDiaryDatabase(
            driver = driver,
            db.Diary.Adapter(dateAdapter = diaryAdapter),
        )
    private val queries = superDiaryDatabase.databaseQueries

    private val mapper = { id: Long, entry: String, date: Instant, favorite: Long ->
        Diary(
            id = id,
            entry = entry,
            date = date,
            isFavorite = favorite.asBoolean(),
        )
    }

    /**
     * This is only used on JVM. Schema is created automatically on Android and
     * iOS
     */
    fun createDatabase() {
        SuperDiaryDatabase.Schema.create(driver)
    }

    fun addDiary(diary: Diary) =
        queries.insert(diary.id, diary.entry, diary.date, diary.isFavorite.asLong())

    fun deleteDiary(id: Long) = queries.delete(id)

    suspend fun deleteDiaries(ids: List<Long>): Int = suspendCoroutine { continuation ->
        queries.transaction {
            afterCommit {
                continuation.resumeWith(
                    kotlin.Result.success(ids.size),
                )
            }
            ids.forEach { id ->
                deleteDiary(id)
            }
        }
    }

    fun getAllDiaries(): Flow<List<Diary>> = queries.selectAll(
        mapper = mapper,
    )
        .asFlow()
        .mapToList(Dispatchers.Main)

    fun findDiaryByEntry(query: String): Flow<List<Diary>> =
        queries.findByEntry(name = query, mapper = mapper)
            .asFlow()
            .mapToList(Dispatchers.Main)

    fun findByDate(date: Instant): Flow<List<Diary>> = queries.findByDate(
        date = date,
        mapper = mapper,
    )
        .asFlow()
        .mapToList(Dispatchers.Main)

    fun findByDateRange(from: Instant, to: Instant): Flow<List<Diary>> =
        queries.findByDateRange(
            from,
            to,
            mapper,
        )
            .asFlow()
            .mapToList(Dispatchers.Main)

    fun update(diary: Diary): Int {
        queries.update(
            id = diary.id,
            entry = diary.entry,
            date = diary.date,
            favorite = diary.isFavorite.asLong(),
        )

        return queries.getAffectedRows().executeAsOne().toInt()
    }

    fun getFavoriteDiaries(): Flow<List<Diary>> =
        queries.getFavoriteDiaries(mapper)
            .asFlow()
            .mapToList(Dispatchers.Main)

    fun clearDiaries() = queries.deleteAll()

    private fun Boolean.asLong(): Long = if (this) 1 else 0
    private fun Long.asBoolean(): Boolean = this != 0L
    fun getLatestEntries(count: Int): Flow<List<Diary>> =
        queries.getLatestEntries(count.toLong(), mapper)
            .asFlow()
            .mapToList(Dispatchers.Main)

    fun countEntries(): Long = queries.countEntries().executeAsOne()

    fun getWeeklySummary(): WeeklySummary? = queries.getWeeklySummary(mapper = { date, summary ->
        WeeklySummary(
            summary = summary,
            date = Instant.parse(date),
        )
    }).executeAsOneOrNull()

    fun insertWeeklySummary(summary: WeeklySummary) {
        queries.transaction {
            queries.clearWeeklySummary()
            queries.insertSummary(summary.summary, summary.date.toString())
        }
    }
}
