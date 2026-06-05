package com.foreverrafs.superdiary.data.datasource

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.map
import com.foreverrafs.superdiary.data.mapper.toDiary
import com.foreverrafs.superdiary.data.mapper.toWeeklySummary
import com.foreverrafs.superdiary.database.Database
import com.foreverrafs.superdiary.database.model.DiaryDb
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.WeeklySummary
import com.foreverrafs.superdiary.domain.model.toDatabase
import com.foreverrafs.superdiary.domain.repository.DataSource
import kotlin.time.Clock
import kotlin.time.Instant
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
class LocalDataSource(
    internal val database: Database,
    private val clock: Clock = Clock.System,
) : DataSource {
    override suspend fun save(diary: Diary): Long =
        database.insert(diary.markDirty(clock).toDatabase())

    override suspend fun save(diaries: List<Diary>): Long =
        database.insert(diaries.map { it.markDirty(clock).toDatabase() })

    override suspend fun delete(diaries: List<Diary>): Int =
        database.markDiariesDeleted(
            ids = diaries.mapNotNull { it.id },
            updatedAt = clock.now(),
        )

    override fun fetchAllPaged(): Flow<PagingData<Diary>> = pager {
        database.getAllDiariesPagingSource()
    }

    override fun fetchFavoritesPaged(): Flow<PagingData<Diary>> = pager {
        database.getFavoriteDiariesPagingSource()
    }

    override fun findPaged(entry: String): Flow<PagingData<Diary>> = pager {
        database.findDiaryByEntryPagingSource(entry)
    }

    override fun findPaged(from: Instant, to: Instant): Flow<PagingData<Diary>> =
        pager {
            database.findByDateRangePagingSource(from, to)
        }

    override fun findPaged(
        entry: String,
        from: Instant,
        to: Instant,
    ): Flow<PagingData<Diary>> = pager {
        database.findByEntryAndDateRangePagingSource(
            query = entry,
            from = from,
            to = to,
        )
    }

    override fun find(id: Long): Diary? = database.findById(id)?.toDiary()

    /**
     * The dates are currently stored on the database as very high precision
     * Long, making it almost impossible to perform equality checks.
     *
     * To look for entries on a particular day, we therefore look for all
     * entries from start of the day 00:00 to midnight 23:59:59
     */

    override fun findByDatePaged(date: Instant): Flow<PagingData<Diary>> {
        val (startOfDay, endOfDay) = date.dayRange()

        return pager {
            database.findByDateRangePagingSource(startOfDay, endOfDay)
        }
    }

    override suspend fun update(diary: Diary): Int =
        database.update(diary.markDirty(clock).toDatabase()).toInt()

    override suspend fun deleteAll() = database.clearDiaries()

    override fun getLatest(count: Int): Flow<List<Diary>> =
        database.getLatestEntries(count).mapToDiary()

    override suspend fun count(): Long = database.countEntries()

    override suspend fun save(summary: WeeklySummary) =
        database.insertWeeklySummary(summary = summary.toDatabase())

    override fun getOne(): WeeklySummary? = database.getWeeklySummary()?.toWeeklySummary()
    override suspend fun clearChatMessages() {
        database.clearChatMessages()
    }

    private fun Flow<List<DiaryDb>>?.mapToDiary() =
        this?.map { diaryDtoList -> diaryDtoList.map { it.toDiary() } }
            ?: emptyFlow()

    private fun pager(pagingSourceFactory: () -> PagingSource<Int, DiaryDb>): Flow<PagingData<Diary>> =
        Pager(
            config = PagingConfig(
                pageSize = DIARY_PAGE_SIZE,
                initialLoadSize = DIARY_PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = pagingSourceFactory,
        ).flow.map { pagingData ->
            pagingData.map { it.toDiary() }
        }

    private fun Instant.dayRange(): Pair<Instant, Instant> {
        val timeZone = TimeZone.currentSystemDefault()
        val currentDate = toLocalDateTime(timeZone).date

        val startOfDay = currentDate.atStartOfDayIn(timeZone)
        val endOfDay = LocalDateTime(
            year = currentDate.year,
            month = currentDate.month.number,
            day = currentDate.day,
            hour = 23,
            minute = 59,
            second = 59,
            nanosecond = 999_999_999,
        ).toInstant(timeZone)

        return startOfDay to endOfDay
    }

    private fun Diary.markDirty(clock: Clock): Diary = copy(
        updatedAt = clock.now(),
        isSynced = false,
        isMarkedForDelete = false,
    )

    private companion object {
        const val DIARY_PAGE_SIZE = 30
    }
}
