package com.foreverrafs.superdiary.datasource

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.first
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.foreverrafs.superdiary.core.location.Location
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.datasource.InitialSyncState
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.data.datasource.OfflineFirstDataSource
import com.foreverrafs.superdiary.data.datasource.remote.DiaryApi
import com.foreverrafs.superdiary.data.mapper.toDiary
import com.foreverrafs.superdiary.data.model.DiaryDto
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.database.model.DiaryChatMessageDb
import com.foreverrafs.superdiary.database.model.DiaryDb
import com.foreverrafs.superdiary.database.testSuperDiaryDatabase
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.WeeklySummary
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant
import kotlin.time.TimeSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

@OptIn(ExperimentalCoroutinesApi::class)
class OfflineFirstDataSourceTest {
    private val database = Database(testSuperDiaryDatabase)
    private val fixedClock = object : Clock {
        override fun now(): Instant = Instant.fromEpochMilliseconds(1_000)
    }
    private lateinit var api: FakeDiaryApi
    private lateinit var localDataSource: LocalDataSource
    private lateinit var dataSource: OfflineFirstDataSource

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        database.clearDiaries()
        api = FakeDiaryApi()
        localDataSource = LocalDataSource(database = database, clock = fixedClock)
        dataSource = createDataSource(api)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `hard deletes on server remove synced local rows`() = runTest {
        insertSyncedDiary(id = 1, entry = "A")

        dataSource.fetchAll().test {
            val initial = awaitItem()
            assertThat(initial.size).isEqualTo(1)

            api.emitSnapshot(emptyList())

            val afterDelete = awaitItem()
            assertThat(afterDelete).isEmpty()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `soft delete events from server remove local rows`() = runTest {
        insertSyncedDiary(id = 2, entry = "B")

        dataSource.fetchAll().test {
            val initial = awaitItem()
            assertThat(initial.size).isEqualTo(1)

            api.emitSnapshot(
                listOf(
                    DiaryDto(
                        entry = "B",
                        id = 2,
                        date = fixedClock.now(),
                        isFavorite = false,
                        location = Location.Empty.toString(),
                        updatedAt = 2_000,
                        isDeleted = true,
                    ),
                ),
            )

            val afterDelete = awaitItem()
            assertThat(afterDelete).isEmpty()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `local delete is pushed to server and removed locally`() = runTest {
        val savedId = dataSource.save(
            Diary(
                entry = "C",
                date = fixedClock.now(),
                updatedAt = fixedClock.now(),
            ),
        )

        dataSource.fetchAll().test {
            val initial = awaitItem()
            assertThat(initial.size).isEqualTo(1)

            val diary = initial.first()
            assertThat(diary.id).isEqualTo(savedId)

            dataSource.delete(listOf(diary))

            awaitUntil { database.getPendingDeleteDiaries().isEmpty() }
            awaitUntil { database.findById(savedId) == null }

            val afterDelete = awaitItem()
            assertThat(afterDelete).isEmpty()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `local save is pushed and marked synced`() = runTest {
        val result = dataSource.save(
            Diary(
                entry = "D",
                date = fixedClock.now(),
                updatedAt = fixedClock.now(),
            ),
        )
        assertThat(result).isEqualTo(1L)

        awaitUntil { api.savedDtos().isNotEmpty() }

        val saved = api.savedDtos().first()
        assertThat(saved.id).isNotNull()

        val savedId = saved.id!!
        awaitUntil {
            val stored = database.findById(savedId)
            stored?.isSynced == true
        }
    }

    @Test
    fun `save list inserts all diaries`() = runTest {
        val result = dataSource.save(
            listOf(
                Diary(entry = "L1", date = fixedClock.now(), updatedAt = fixedClock.now()),
                Diary(entry = "L2", date = fixedClock.now(), updatedAt = fixedClock.now()),
            ),
        )

        assertThat(result).isEqualTo(2L)
        assertThat(dataSource.count()).isEqualTo(2L)
    }

    @Test
    fun `update diary persists changes`() = runTest {
        val id = dataSource.save(
            Diary(
                entry = "Old",
                date = fixedClock.now(),
                updatedAt = fixedClock.now(),
            ),
        )

        val original = dataSource.find(id)
        assertThat(original).isNotNull()

        val updated = original!!.copy(entry = "New")
        val result = dataSource.update(updated)

        assertThat(result).isEqualTo(1)
        assertThat(dataSource.find(id)?.entry).isEqualTo("New")
    }

    @Test
    fun `fetch favorites returns only favorite entries`() = runTest {
        insertDiary(
            id = 20,
            entry = "Fav",
            updatedAt = Instant.fromEpochMilliseconds(1_000),
            isSynced = true,
            isFavorite = true,
        )
        insertDiary(
            id = 21,
            entry = "Normal",
            updatedAt = Instant.fromEpochMilliseconds(1_000),
            isSynced = true,
            isFavorite = false,
        )

        dataSource.fetchFavorites().test {
            val favorites = awaitItem()
            assertThat(favorites.size).isEqualTo(1)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `find by entry returns matching diaries`() = runTest {
        insertDiary(
            id = 22,
            entry = "Hello World",
            updatedAt = Instant.fromEpochMilliseconds(1_000),
            isSynced = true,
        )
        insertDiary(
            id = 23,
            entry = "Other",
            updatedAt = Instant.fromEpochMilliseconds(1_000),
            isSynced = true,
        )

        dataSource.find("Hello").test {
            val results = awaitItem()
            assertThat(results.size).isEqualTo(1)
            assertThat(results.first().entry).isEqualTo("Hello World")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `find by date returns entries from same day`() = runTest {
        val timeZone = TimeZone.currentSystemDefault()
        val targetDate = LocalDateTime(2020, 1, 2, 12, 0).toInstant(timeZone)
        val otherDate = LocalDateTime(2020, 1, 3, 12, 0).toInstant(timeZone)

        insertDiary(
            id = 24,
            entry = "Target",
            updatedAt = Instant.fromEpochMilliseconds(1_000),
            isSynced = true,
            date = targetDate,
        )
        insertDiary(
            id = 25,
            entry = "Other",
            updatedAt = Instant.fromEpochMilliseconds(1_000),
            isSynced = true,
            date = otherDate,
        )

        dataSource.findByDate(targetDate).test {
            val results = awaitItem()
            assertThat(results.size).isEqualTo(1)
            assertThat(results.first().entry).isEqualTo("Target")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `find by date range returns entries within bounds`() = runTest {
        val timeZone = TimeZone.currentSystemDefault()
        val start = LocalDateTime(2020, 2, 1, 12, 0).toInstant(timeZone)
        val middle = LocalDateTime(2020, 2, 2, 12, 0).toInstant(timeZone)
        val end = LocalDateTime(2020, 2, 3, 12, 0).toInstant(timeZone)

        insertDiary(
            id = 26,
            entry = "Start",
            updatedAt = Instant.fromEpochMilliseconds(1_000),
            isSynced = true,
            date = start,
        )
        insertDiary(
            id = 27,
            entry = "Middle",
            updatedAt = Instant.fromEpochMilliseconds(1_000),
            isSynced = true,
            date = middle,
        )
        insertDiary(
            id = 28,
            entry = "End",
            updatedAt = Instant.fromEpochMilliseconds(1_000),
            isSynced = true,
            date = end,
        )

        dataSource.find(start, end).test {
            val results = awaitItem()
            assertThat(results.size).isEqualTo(3)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `get latest returns most recent diaries`() = runTest {
        val timeZone = TimeZone.currentSystemDefault()
        val oldest = LocalDateTime(2020, 3, 1, 12, 0).toInstant(timeZone)
        val middle = LocalDateTime(2020, 3, 2, 12, 0).toInstant(timeZone)
        val newest = LocalDateTime(2020, 3, 3, 12, 0).toInstant(timeZone)

        insertDiary(
            id = 29,
            entry = "Old",
            updatedAt = Instant.fromEpochMilliseconds(1_000),
            isSynced = true,
            date = oldest,
        )
        insertDiary(
            id = 30,
            entry = "Mid",
            updatedAt = Instant.fromEpochMilliseconds(1_000),
            isSynced = true,
            date = middle,
        )
        insertDiary(
            id = 31,
            entry = "New",
            updatedAt = Instant.fromEpochMilliseconds(1_000),
            isSynced = true,
            date = newest,
        )

        dataSource.getLatest(2).test {
            val results = awaitItem()
            assertThat(results.size).isEqualTo(2)
            assertThat(results[0].entry).isEqualTo("New")
            assertThat(results[1].entry).isEqualTo("Mid")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `count returns number of entries`() = runTest {
        insertDiary(
            id = 32,
            entry = "One",
            updatedAt = Instant.fromEpochMilliseconds(1_000),
            isSynced = true,
        )
        insertDiary(
            id = 33,
            entry = "Two",
            updatedAt = Instant.fromEpochMilliseconds(1_000),
            isSynced = true,
        )

        assertThat(dataSource.count()).isEqualTo(2L)
    }

    @Test
    fun `save summary and get one returns summary`() = runTest {
        val summary = WeeklySummary(summary = "Week", date = fixedClock.now())
        dataSource.save(summary)

        val stored = dataSource.getOne()
        assertThat(stored?.summary).isEqualTo("Week")
    }

    @Test
    fun `clear chat messages removes stored chats`() = runTest {
        database.saveChatMessage(DiaryChatMessageDb.User("hi"))
        assertThat(database.getChatMessages().first().isEmpty()).isEqualTo(false)

        dataSource.clearChatMessages()

        assertThat(database.getChatMessages().first().isEmpty()).isEqualTo(true)
    }

    @Test
    fun `delete all clears diaries`() = runTest {
        insertDiary(
            id = 34,
            entry = "One",
            updatedAt = Instant.fromEpochMilliseconds(1_000),
            isSynced = true,
        )

        dataSource.deleteAll()

        assertThat(dataSource.count()).isEqualTo(0L)
    }

    @Test
    fun `find by id returns diary when present`() = runTest {
        val id = insertDiary(
            id = 35,
            entry = "FindMe",
            updatedAt = Instant.fromEpochMilliseconds(1_000),
            isSynced = true,
        )

        val diary = dataSource.find(id)
        assertThat(diary?.entry).isEqualTo("FindMe")
    }

    @Test
    fun `remote upsert newer overwrites local entry`() = runTest {
        val localId = insertDiary(
            id = 10,
            entry = "local",
            updatedAt = Instant.fromEpochMilliseconds(1_000),
            isSynced = true,
        )

        api.emitSnapshot(
            listOf(
                DiaryDto(
                    entry = "remote",
                    id = localId,
                    date = fixedClock.now(),
                    isFavorite = false,
                    location = Location.Empty.toString(),
                    updatedAt = 2_000,
                    isDeleted = false,
                ),
            ),
        )

        awaitUntil {
            val stored = database.findById(localId)
            stored != null && stored.entry == "remote" && stored.isSynced && !stored.isMarkedForDelete
        }
    }

    @Test
    fun `remote upsert older is ignored`() = runTest {
        val localId = insertDiary(
            id = 11,
            entry = "local",
            updatedAt = Instant.fromEpochMilliseconds(3_000),
            isSynced = true,
        )

        api.emitSnapshot(
            listOf(
                DiaryDto(
                    entry = "remote",
                    id = localId,
                    date = fixedClock.now(),
                    isFavorite = false,
                    location = Location.Empty.toString(),
                    updatedAt = 2_000,
                    isDeleted = false,
                ),
                DiaryDto(
                    entry = "new",
                    id = 99,
                    date = fixedClock.now(),
                    isFavorite = false,
                    location = Location.Empty.toString(),
                    updatedAt = 4_000,
                    isDeleted = false,
                ),
            ),
        )

        awaitUntil { database.findById(99) != null }

        val stored = database.findById(localId)
        assertThat(stored?.entry).isEqualTo("local")
        assertThat(stored?.updatedAt).isEqualTo(Instant.fromEpochMilliseconds(3_000))
    }

    @Test
    fun `remote delete with equal timestamp applies`() = runTest {
        val localId = insertDiary(
            id = 12,
            entry = "local",
            updatedAt = Instant.fromEpochMilliseconds(2_000),
            isSynced = true,
        )

        api.emitSnapshot(
            listOf(
                DiaryDto(
                    entry = "local",
                    id = localId,
                    date = fixedClock.now(),
                    isFavorite = false,
                    location = Location.Empty.toString(),
                    updatedAt = 2_000,
                    isDeleted = true,
                ),
            ),
        )

        awaitUntil { database.findById(localId) == null }
    }

    @Test
    fun `remote delete is ignored when local is newer`() = runTest {
        val localId = insertDiary(
            id = 13,
            entry = "local",
            updatedAt = Instant.fromEpochMilliseconds(3_000),
            isSynced = true,
        )

        api.emitSnapshot(
            listOf(
                DiaryDto(
                    entry = "local",
                    id = localId,
                    date = fixedClock.now(),
                    isFavorite = false,
                    location = Location.Empty.toString(),
                    updatedAt = 2_000,
                    isDeleted = true,
                ),
                DiaryDto(
                    entry = "remote",
                    id = 100,
                    date = fixedClock.now(),
                    isFavorite = false,
                    location = Location.Empty.toString(),
                    updatedAt = 4_000,
                    isDeleted = false,
                ),
            ),
        )

        awaitUntil { database.findById(100) != null }
        assertThat(database.findById(localId)).isNotNull()
    }

    @Test
    fun `hard deletes do not remove unsynced local rows`() = runTest {
        val syncedId = insertDiary(
            id = 14,
            entry = "synced",
            updatedAt = Instant.fromEpochMilliseconds(1_000),
            isSynced = true,
        )
        val unsyncedId = insertDiary(
            id = 15,
            entry = "unsynced",
            updatedAt = Instant.fromEpochMilliseconds(1_500),
            isSynced = false,
        )

        api.emitSnapshot(emptyList())

        awaitUntil { database.findById(syncedId) == null }
        assertThat(database.findById(unsyncedId)).isNotNull()
    }

    @Test
    fun `initial sync completes after first snapshot`() = runTest {
        api.emitSnapshot(emptyList())

        awaitUntil { dataSource.initialSyncState.value == InitialSyncState.Completed }
        assertThat(dataSource.initialSyncState.value).isEqualTo(InitialSyncState.Completed)
    }

    @Test
    fun `initial sync fails when realtime subscription errors`() = runTest {
        val failingApi = FakeDiaryApi(
            fetchAllFlow = flow { throw IllegalStateException("boom") },
        )
        dataSource = createDataSource(failingApi)

        awaitUntil { dataSource.initialSyncState.value == InitialSyncState.Failed }
        assertThat(dataSource.initialSyncState.value).isEqualTo(InitialSyncState.Failed)
    }

    @Test
    fun `save failure keeps diary unsynced`() = runTest {
        val failingApi = FakeDiaryApi(failSave = true)
        dataSource = createDataSource(failingApi)

        val id = dataSource.save(
            Diary(
                entry = "E",
                date = fixedClock.now(),
                updatedAt = fixedClock.now(),
            ),
        )

        val stored = database.findById(id)
        assertThat(stored?.isSynced).isEqualTo(false)
    }

    @Test
    fun `delete failure keeps pending delete`() = runTest {
        val failingApi = FakeDiaryApi(failDelete = true)
        dataSource = createDataSource(failingApi)

        val id = dataSource.save(
            Diary(
                entry = "F",
                date = fixedClock.now(),
                updatedAt = fixedClock.now(),
            ),
        )

        val diary = database.findById(id)?.toDiary()
        assertThat(diary).isNotNull()

        dataSource.delete(listOf(diary!!))

        val pendingDeletes = database.getPendingDeleteDiaries()
        assertThat(pendingDeletes.isNotEmpty()).isEqualTo(true)
    }

    private fun insertSyncedDiary(id: Long, entry: String): Long {
        database.upsert(
            DiaryDb(
                id = id,
                entry = entry,
                date = fixedClock.now(),
                isFavorite = false,
                updatedAt = fixedClock.now(),
                isSynced = true,
                isMarkedForDelete = false,
                location = Location.Empty.toString(),
            ),
        )

        return id
    }

    private fun insertDiary(
        id: Long,
        entry: String,
        updatedAt: Instant,
        isSynced: Boolean,
        isMarkedForDelete: Boolean = false,
        isFavorite: Boolean = false,
        date: Instant = fixedClock.now(),
    ): Long {
        database.upsert(
            DiaryDb(
                id = id,
                entry = entry,
                date = date,
                isFavorite = isFavorite,
                updatedAt = updatedAt,
                isSynced = isSynced,
                isMarkedForDelete = isMarkedForDelete,
                location = Location.Empty.toString(),
            ),
        )

        return id
    }

    private suspend fun awaitUntil(
        timeoutMs: Long = 3_000,
        intervalMs: Long = 50,
        condition: () -> Boolean,
    ) {
        val start = TimeSource.Monotonic.markNow()
        while (!condition()) {
            if (start.elapsedNow() > timeoutMs.milliseconds) {
                error("Condition not met within ${timeoutMs}ms")
            }
            delay(intervalMs)
        }
    }

    private fun createDataSource(api: FakeDiaryApi): OfflineFirstDataSource =
        OfflineFirstDataSource(
            database = localDataSource,
            diaryApi = api,
            logger = AggregateLogger(),
            clock = fixedClock,
        )

    private class FakeDiaryApi(
        private val fetchAllFlow: Flow<List<DiaryDto>>? = null,
        private val failSave: Boolean = false,
        private val failDelete: Boolean = false,
    ) : DiaryApi {
        private val updates = MutableSharedFlow<List<DiaryDto>>(replay = 1)
        private val saved = MutableStateFlow<List<DiaryDto>>(emptyList())
        private val deleted = MutableStateFlow<List<DiaryDto>>(emptyList())

        override fun fetchAll(): Flow<List<DiaryDto>> = fetchAllFlow ?: updates

        override suspend fun save(diary: DiaryDto): Result<Boolean> = if (failSave) {
            Result.Failure(IllegalStateException("save failed"))
        } else {
            saved.update { it + diary }
            Result.Success(true)
        }

        override suspend fun fetch(count: Int): Result<List<DiaryDto>> =
            Result.Success(emptyList())

        override suspend fun countItems(): Result<Long> = Result.Success(0L)

        override suspend fun delete(diary: DiaryDto): Result<Boolean> = if (failDelete) {
            Result.Failure(IllegalStateException("delete failed"))
        } else {
            deleted.update { it + diary }
            Result.Success(true)
        }

        suspend fun emitSnapshot(diaries: List<DiaryDto>) {
            updates.emit(diaries)
        }

        fun savedDtos(): List<DiaryDto> = saved.value
    }
}
