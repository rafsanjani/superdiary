package com.foreverrafs.superdiary.data.datasource

import com.foreverrafs.superdiary.data.mapper.toDiary
import com.foreverrafs.superdiary.data.mapper.toWeeklySummary
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.database.model.DiaryDb
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.WeeklySummary
import com.foreverrafs.superdiary.domain.model.toDatabase
import com.foreverrafs.superdiary.domain.repository.DataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@Suppress("TooManyFunctions")
class LocalDataSource(private val database: Database) : DataSource {
    override suspend fun add(diary: Diary): Long = database.insert(diary.toDatabase())

    override suspend fun addAll(diaries: List<Diary>): Long =
        database.insert(diaries.map { it.toDatabase() })

    override suspend fun delete(diaries: List<Diary>): Int =
        database.deleteDiaries(diaries.mapNotNull { it.id })

    override fun fetchAll(): Flow<List<Diary>> = database.getAllDiaries().mapToDiary()

    override fun fetchFavorites(): Flow<List<Diary>> = database.getFavoriteDiaries().mapToDiary()

    override fun find(entry: String): Flow<List<Diary>> =
        database.findDiaryByEntry(entry).mapToDiary()

    override fun find(from: kotlin.time.Instant, to: kotlin.time.Instant): Flow<List<Diary>> =
        database.findByDateRange(from, to).map { diaryDbList ->
            diaryDbList.map { it.toDiary() }
        }

    override fun find(id: Long): Diary? = database.findById(id)?.toDiary()

    /**
     * The dates are currently stored on the database as very high precision
     * Long, making it almost impossible to perform equality checks.
     *
     * To look for entries on a particular day, we therefore look for all
     * entries from start of the day 00:00 to midnight 23:59:59
     */

    override fun findByDate(date: kotlin.time.Instant): Flow<List<Diary>> {
        val timeZone = TimeZone.currentSystemDefault()

        val currentLocalDateTime = date.toLocalDateTime(timeZone)
        val currentDate = currentLocalDateTime.date

        // Start of day
        val startOfDay = currentDate.atStartOfDayIn(timeZone)

        // End of day
        val endOfDay = LocalDateTime(
            year = currentDate.year,
            month = currentDate.month.number,
            day = currentDate.day,
            hour = 23,
            minute = 59,
            second = 59,
        ).toInstant(timeZone)

        return database.findByDateRange(startOfDay, endOfDay).mapToDiary()
    }

    override suspend fun update(diary: Diary): Int = database.update(diary.toDatabase()).toInt()

    override suspend fun deleteAll() = database.clearDiaries()

    override fun getLatestEntries(count: Int): Flow<List<Diary>> =
        database.getLatestEntries(count).mapToDiary()

    override suspend fun countEntries(): Long = database.countEntries()

    override suspend fun insertWeeklySummary(summary: WeeklySummary) =
        database.insertWeeklySummary(summary = summary.toDatabase())

    override fun getWeeklySummary(): WeeklySummary? = database.getWeeklySummary()?.toWeeklySummary()
    override suspend fun clearChatMessages() {
        database.clearChatMessages()
    }

    private fun Flow<List<DiaryDb>>?.mapToDiary() =
        this?.map { diaryDtoList -> diaryDtoList.map { it.toDiary() } }
            ?: emptyFlow()

    override suspend fun getPendingDeletes(): List<Diary> =
        database.getPendingDeletes().map { it.toDiary() }

    override suspend fun getPendingSyncs(): List<Diary> =
        database.getPendingSyncs().map { it.toDiary() }
}
