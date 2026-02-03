package com.foreverrafs.superdiary.database

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.foreverrafs.superdiary.database.model.DiaryChatMessageDb
import com.foreverrafs.superdiary.database.model.DiaryChatRoleDb
import com.foreverrafs.superdiary.database.model.DiaryDb
import com.foreverrafs.superdiary.database.model.LocationDb
import com.foreverrafs.superdiary.database.model.WeeklySummaryDb
import kotlin.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine

// Converts a date to a long value and vice versa
val dateAdapter = object : ColumnAdapter<Instant, Long> {
    override fun decode(databaseValue: Long): Instant = Instant.fromEpochMilliseconds(databaseValue)

    override fun encode(value: Instant): Long = value.toEpochMilliseconds()
}

@Suppress("TooManyFunctions")
class Database(
    database: SuperDiaryDatabase,
) {
    private val queries = database.databaseQueries

    private val diaryMapper =
        { id: Long, entry: String, date: Instant, favorite: Long, location: LocationDb? ->
            DiaryDb(
                id = id,
                entry = entry,
                date = date,
                isFavorite = favorite.asBoolean(),
                location = location.toString(),
            )
        }

    fun insert(diary: DiaryDb): Long {
        queries.insert(
            id = diary.id,
            entry = diary.entry,
            date = diary.date,
            favorite = diary.isFavorite.asLong(),
            location = LocationDb.fromString(diary.location),
        )

        return queries.lastInsertRowId().executeAsOne()
    }

    fun upsert(diary: DiaryDb): Long = if (diary.id == null) {
        insert(diary)
    } else {
        update(diary)
    }

    fun insert(diaries: List<DiaryDb>): Long {
        val result = queries.transactionWithResult {
            diaries.forEach(::upsert)
            diaries.size.toLong()
        }

        return result
    }

    private fun deleteDiary(id: Long) = queries.delete(id)

    suspend fun deleteDiaries(ids: List<Long>): Int = suspendCancellableCoroutine { continuation ->
        queries.transaction {
            afterCommit {
                continuation.resumeWith(
                    Result.success(
                        queries.getAffectedRows().executeAsOne().toInt(),
                    ),
                )
            }

            ids.forEach { id ->
                deleteDiary(id)
            }
        }
    }

    fun findById(id: Long): DiaryDb? = queries.findById(id, diaryMapper).executeAsOneOrNull()

    fun getAllDiaries(): Flow<List<DiaryDb>> = queries.selectAll(
        mapper = diaryMapper,
    ).asFlow().mapToList(Dispatchers.Main)

    fun findDiaryByEntry(query: String): Flow<List<DiaryDb>> =
        queries.findByEntry(name = query, mapper = diaryMapper).asFlow().mapToList(Dispatchers.Main)

    fun findByDateRange(from: Instant, to: Instant): Flow<List<DiaryDb>> = queries.findByDateRange(
        from,
        to,
        diaryMapper,
    ).asFlow().mapToList(Dispatchers.Main)

    fun update(diary: DiaryDb): Long {
        queries.update(
            id = diary.id,
            entry = diary.entry,
            date = diary.date,
            favorite = diary.isFavorite.asLong(),
            location = LocationDb.fromString(diary.location),
        )

        return queries.getAffectedRows().executeAsOne()
    }

    fun getFavoriteDiaries(): Flow<List<DiaryDb>> =
        queries.getFavoriteDiaries(diaryMapper).asFlow().mapToList(Dispatchers.Main)

    fun clearDiaries() {
        queries.deleteAll()
    }

    private fun Boolean.asLong(): Long = if (this) 1 else 0
    private fun Long.asBoolean(): Boolean = this != 0L
    fun getLatestEntries(count: Int): Flow<List<DiaryDb>> =
        queries.getLatestEntries(count.toLong(), diaryMapper).asFlow().mapToList(Dispatchers.Main)

    fun countEntries(): Long = queries.countEntries().executeAsOne()

    fun getWeeklySummary(): WeeklySummaryDb? = queries.getWeeklySummary(
        mapper = { date, summary ->
            WeeklySummaryDb(
                summary = summary,
                date = Instant.parse(date),
            )
        },
    ).executeAsOneOrNull()

    fun insertWeeklySummary(summary: WeeklySummaryDb) {
        queries.transaction {
            queries.clearWeeklySummary()
            queries.insertSummary(summary.summary, summary.date.toString())
        }
    }

    fun saveChatMessage(message: DiaryChatMessageDb) {
        queries.saveChat(
            id = message.id,
            date = message.timestamp,
            content = message.content,
            role = message.role,
        )
    }

    fun clearChatMessages() {
        queries.clearChat()
    }

    fun getChatMessages(): Flow<List<DiaryChatMessageDb>> = queries.getChatMessages(
        mapper = { id: String, timestamp: Instant, content: String, role: DiaryChatRoleDb ->
            DiaryChatMessageDb(
                id = id,
                role = role,
                content = content,
                timestamp = timestamp,
            )
        },
    ).asFlow().mapToList(Dispatchers.Main)
}
