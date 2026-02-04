package com.foreverrafs.superdiary.datasource

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.foreverrafs.superdiary.core.location.Location
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.datasource.LocalDataSource
import com.foreverrafs.superdiary.data.datasource.OfflineFirstDataSource
import com.foreverrafs.superdiary.data.datasource.remote.DiaryApi
import com.foreverrafs.superdiary.data.model.DiaryDto
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.database.model.DiaryDb
import com.foreverrafs.superdiary.database.testSuperDiaryDatabase
import com.foreverrafs.superdiary.domain.model.Diary
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class OfflineFirstDataSourceTest {
    private val database = Database(testSuperDiaryDatabase)
    private val fixedClock = object : Clock {
        override fun now(): Instant = Instant.fromEpochMilliseconds(1_000)
    }
    private lateinit var api: FakeDiaryApi
    private lateinit var local: LocalDataSource
    private lateinit var dataSource: OfflineFirstDataSource

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        database.clearDiaries()
        api = FakeDiaryApi()
        local = LocalDataSource(database = database, clock = fixedClock)
        dataSource = OfflineFirstDataSource(
            database = local,
            diaryApi = api,
            logger = AggregateLogger(),
            clock = fixedClock,
        )
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

    private fun insertSyncedDiary(id: Long, entry: String): Long = database.upsert(
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

    private class FakeDiaryApi : DiaryApi {
        private val updates = MutableSharedFlow<List<DiaryDto>>(replay = 1)
        private val saved = MutableStateFlow<List<DiaryDto>>(emptyList())
        private val deleted = MutableStateFlow<List<DiaryDto>>(emptyList())

        override fun fetchAll(): Flow<List<DiaryDto>> = updates

        override suspend fun save(diary: DiaryDto): Result<Boolean> {
            saved.update { it + diary }
            return Result.Success(true)
        }

        override suspend fun fetch(count: Int): Result<List<DiaryDto>> =
            Result.Success(emptyList())

        override suspend fun countItems(): Result<Long> = Result.Success(0L)

        override suspend fun delete(diary: DiaryDto): Result<Boolean> {
            deleted.update { it + diary }
            return Result.Success(true)
        }

        suspend fun emitSnapshot(diaries: List<DiaryDto>) {
            updates.emit(diaries)
        }

        fun savedDtos(): List<DiaryDto> = saved.value

        fun deletedIds(): List<Long> = deleted.value.mapNotNull { it.id }
    }
}
