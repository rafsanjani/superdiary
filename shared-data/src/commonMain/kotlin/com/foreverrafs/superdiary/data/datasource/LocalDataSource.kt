package com.foreverrafs.superdiary.data.datasource

import com.foreverrafs.superdiary.data.Database
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.model.WeeklySummary
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@Suppress("TooManyFunctions")
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

    override suspend fun delete(diaries: List<Diary>): Int {
        return database.deleteDiaries(diaries.mapNotNull { it.id })
    }

    override fun fetchAll(): Flow<List<Diary>> {
        return database.getAllDiaries()
    }

    override fun fetchFavorites(): Flow<List<Diary>> {
        return database.getFavoriteDiaries()
    }

    override fun find(entry: String): Flow<List<Diary>> {
        return database.findDiaryByEntry(entry)
    }

    override fun find(from: Instant, to: Instant): Flow<List<Diary>> {
        return database.findByDateRange(from, to)
    }

    /**
     * The dates are currently stored on the database as very high
     * precision Long, making it almost impossible to perform equality
     * checks.
     *
     * To look for entries on a particular day, we therefore
     * look for all entries from start of the day 00:00 to midnight 23:59:59
     */

    override fun findByDate(date: Instant): Flow<List<Diary>> {
        val timeZone = TimeZone.currentSystemDefault()

        val currentLocalDateTime = date.toLocalDateTime(timeZone)
        val currentDate = currentLocalDateTime.date

        // Start of day
        val startOfDay = currentDate.atStartOfDayIn(timeZone)

        // End of day
        val endOfDay = LocalDateTime(
            currentDate.year,
            currentDate.monthNumber,
            currentDate.dayOfMonth,
            23,
            59,
            59,
        ).toInstant(timeZone)

        return database.findByDateRange(startOfDay, endOfDay)
    }

    override suspend fun update(diary: Diary): Int {
        return database.update(diary)
    }

    override suspend fun deleteAll() {
        return database.clearDiaries()
    }

    override fun getLatestEntries(count: Int): Flow<List<Diary>> {
        return database.getLatestEntries(count)
    }

    override fun countEntries(): Long = database.countEntries()

    override fun insertWeeklySummary(summary: WeeklySummary) =
        database.insertWeeklySummary(summary = summary)

    override fun getWeeklySummary(): WeeklySummary? {
        return database.getWeeklySummary()
    }
}
