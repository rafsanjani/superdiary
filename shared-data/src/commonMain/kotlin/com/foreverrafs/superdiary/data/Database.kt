package com.foreverrafs.superdiary.data

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneNotNull
import com.foreverrafs.superdiary.core.location.Location
import com.foreverrafs.superdiary.data.diaryai.DiaryChatMessage
import com.foreverrafs.superdiary.data.diaryai.DiaryChatRole
import com.foreverrafs.superdiary.data.model.DiaryDto
import com.foreverrafs.superdiary.database.SuperDiaryDatabase
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.WeeklySummary
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

private val dateAdapter = object : ColumnAdapter<Instant, Long> {
    override fun decode(databaseValue: Long): Instant = Instant.fromEpochMilliseconds(databaseValue)

    override fun encode(value: Instant): Long = value.toEpochMilliseconds()
}

private val locationAdapter = object : ColumnAdapter<Location, String> {
    override fun decode(databaseValue: String): Location = Location.fromString(databaseValue)

    override fun encode(value: Location): String = value.toString()
}

@Suppress("TooManyFunctions")
class Database(databaseDriver: DatabaseDriver) {
    private val driver = databaseDriver.createDriver()
    private val superDiaryDatabase =
        SuperDiaryDatabase(
            driver = driver,
            diaryAdapter = db.Diary.Adapter(
                dateAdapter = dateAdapter,
                locationAdapter = locationAdapter,
            ),
            chatAdapter = db.Chat.Adapter(
                dateAdapter = dateAdapter,
                roleAdapter = EnumColumnAdapter<DiaryChatRole>(),
            ),
        )
    private val queries = superDiaryDatabase.databaseQueries

    private val diaryMapper =
        { id: Long, entry: String, date: Instant, favorite: Long, location: Location? ->
            DiaryDto(
                id = id,
                entry = entry,
                date = date,
                isFavorite = favorite.asBoolean(),
                location = location ?: Location.Empty,
            )
        }

    /**
     * This is only used on JVM and in tests. Schema is created automatically
     * on Android and iOS
     */
    fun createDatabase() {
        SuperDiaryDatabase.Schema.create(driver)
    }

    fun addDiary(diary: DiaryDto) =
        queries.insert(
            id = diary.id,
            entry = diary.entry,
            date = diary.date,
            favorite = diary.isFavorite.asLong(),
            location = diary.location,
        )

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

    fun findById(id: Long): Flow<DiaryDto> =
        queries.findById(id, diaryMapper).asFlow().mapToOneNotNull(Dispatchers.Main)

    fun getAllDiaries(): Flow<List<DiaryDto>> = queries.selectAll(
        mapper = diaryMapper,
    )
        .asFlow()
        .mapToList(Dispatchers.Main)

    fun findDiaryByEntry(query: String): Flow<List<DiaryDto>> =
        queries.findByEntry(name = query, mapper = diaryMapper)
            .asFlow()
            .mapToList(Dispatchers.Main)

    fun findByDateRange(from: Instant, to: Instant): Flow<List<DiaryDto>> =
        queries.findByDateRange(
            from,
            to,
            diaryMapper,
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

    fun getFavoriteDiaries(): Flow<List<DiaryDto>> =
        queries.getFavoriteDiaries(diaryMapper)
            .asFlow()
            .mapToList(Dispatchers.Main)

    fun clearDiaries() = queries.deleteAll()

    private fun Boolean.asLong(): Long = if (this) 1 else 0
    private fun Long.asBoolean(): Boolean = this != 0L
    fun getLatestEntries(count: Int): Flow<List<DiaryDto>> =
        queries.getLatestEntries(count.toLong(), diaryMapper)
            .asFlow()
            .mapToList(Dispatchers.Main)

    fun countEntries(): Long = queries.countEntries().executeAsOne()

    fun getWeeklySummary(): WeeklySummary? = queries.getWeeklySummary(
        mapper = { date, summary ->
            WeeklySummary(
                summary = summary,
                date = Instant.parse(date),
            )
        },
    ).executeAsOneOrNull()

    fun insertWeeklySummary(summary: WeeklySummary) {
        queries.transaction {
            queries.clearWeeklySummary()
            queries.insertSummary(summary.summary, summary.date.toString())
        }
    }

    fun saveChatMessage(message: DiaryChatMessage) {
        queries.saveChat(
            id = message.id,
            date = message.timestamp,
            content = message.content,
            role = message.role,
        )
    }

    fun clearChatMessages() = queries.clearChat()

    fun getChatMessages(): Flow<List<DiaryChatMessage>> =
        queries.getChatMessages(
            mapper = { id: String, timestamp: Instant, content: String, role: DiaryChatRole ->
                DiaryChatMessage(
                    id = id,
                    role = role,
                    content = content,
                    timestamp = timestamp,
                )
            },
        ).asFlow().mapToList(Dispatchers.Main)
}
