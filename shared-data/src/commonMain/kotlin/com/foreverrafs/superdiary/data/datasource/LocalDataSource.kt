package com.foreverrafs.superdiary.data.datasource

import com.foreverrafs.superdiary.data.Database
import com.foreverrafs.superdiary.data.diaryai.DiaryChatMessage
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.WeeklySummary
import com.foreverrafs.superdiary.domain.repository.DataSource
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

    override suspend fun delete(diary: Diary): Int = delete(listOf(diary))

    override suspend fun delete(diaries: List<Diary>): Int =
        database.deleteDiaries(diaries.mapNotNull { it.id })

    override fun fetchAll(): Flow<List<Diary>> = database.getAllDiaries()

    override fun fetchFavorites(): Flow<List<Diary>> = database.getFavoriteDiaries()

    override fun find(entry: String): Flow<List<Diary>> = database.findDiaryByEntry(entry)

    override fun find(from: Instant, to: Instant): Flow<List<Diary>> =
        database.findByDateRange(from, to)

    override fun find(id: Long): Flow<Diary?> = database.findById(id)

    /**
     * The dates are currently stored on the database as very high precision
     * Long, making it almost impossible to perform equality checks.
     *
     * To look for entries on a particular day, we therefore look for all
     * entries from start of the day 00:00 to midnight 23:59:59
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

    override suspend fun update(diary: Diary): Int = database.update(diary)

    override suspend fun deleteAll() = database.clearDiaries()

    override fun getLatestEntries(count: Int): Flow<List<Diary>> = database.getLatestEntries(count)

    override suspend fun countEntries(): Long = database.countEntries()

    override suspend fun insertWeeklySummary(summary: WeeklySummary) =
        database.insertWeeklySummary(summary = summary)

    override fun getWeeklySummary(): WeeklySummary? = database.getWeeklySummary()

    override suspend fun saveChatMessage(message: DiaryChatMessage) {
        database.saveChatMessage(message)
    }

    override suspend fun clearChatMessages() {
        database.clearChatMessages()
    }

    override fun getChatMessages(): Flow<List<DiaryChatMessage>> = database.getChatMessages()
}
