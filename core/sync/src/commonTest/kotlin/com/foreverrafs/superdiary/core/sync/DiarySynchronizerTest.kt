@file:OptIn(ExperimentalTime::class)

package com.foreverrafs.superdiary.core.sync

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.datasource.remote.DiaryApi
import com.foreverrafs.superdiary.data.model.DiaryDto
import com.foreverrafs.superdiary.domain.Synchronizer
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@ExperimentalCoroutinesApi
class DiarySynchronizerTest {
    private val diaryApi: DiaryApi = mock {
        every { fetchAll() } returns flowOf(emptyList())
        everySuspend { save(any()) } returns Result.Success(true)
        everySuspend { delete(any()) } returns Result.Success(true)
    }

    private val datasource: DataSource = mock {
        everySuspend { update(any()) } returns 1
        everySuspend { delete(any()) } returns 1
        everySuspend { deleteAll() } returns Unit
        everySuspend { getPendingDeletes() } returns listOf(
            Diary(
                entry = "Hello World",
                isMarkedForDelete = true,
                isSynced = false,
            ),
        )
        everySuspend { getPendingSyncs() } returns listOf(
            Diary(
                entry = "Hello",
                isSynced = false,
            ),
        )
        everySuspend { save(any<List<Diary>>()) } returns 100L
    }
    private val synchronizer = DiarySynchronizer(
        diaryApi = diaryApi,
        dataSource = datasource,
        logger = AggregateLogger(emptyList()),
        appCoroutineDispatchers = TestAppDispatchers,
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(TestAppDispatchers.main)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should load all diary entries when sync is listening`() = runTest {
        synchronizer.startListening()
        advanceUntilIdle()

        verifySuspend(
            mode = VerifyMode.exactly(1),
        ) {
            diaryApi.fetchAll()
        }
    }

    @Test
    fun `Should perform a save sync when there is a pending save after start listening`() =
        runTest {
            synchronizer.startListening()

            verifySuspend { diaryApi.save(any()) }
        }

    @Test
    fun `Should perform a delete sync when there is a pending delete after start listening`() =
        runTest {
            everySuspend { datasource.getPendingDeletes() } returns listOf(
                Diary(
                    entry = "Hello World",
                    isMarkedForDelete = true,
                    isSynced = true,
                ),
            )

            everySuspend { datasource.getPendingSyncs() } returns listOf(
                Diary(
                    entry = "Hello",
                    isSynced = true,
                    isMarkedForDelete = false,
                ),
            )

            synchronizer.startListening()

            verifySuspend { diaryApi.delete(any()) }
        }

    @Test
    fun `Should save entries to remote when a save sync is requested`() = runTest {
        synchronizer.sync(Synchronizer.SyncOperation.Save(Diary("hello")))
        verifySuspend { diaryApi.save(any()) }
    }

    @Test
    fun `Should delete entries from remote followed by a local delete when a delete sync is requested`() =
        runTest {
            synchronizer.sync(Synchronizer.SyncOperation.Delete(Diary("hello")))
            verifySuspend { diaryApi.delete(any()) }
            verifySuspend { datasource.delete(any()) }
        }

    @Test
    fun `Should return TRUE when SAVE sync operation succeeds`() = runTest {
        val result = synchronizer.sync(Synchronizer.SyncOperation.Save(Diary("hello")))
        assertThat(result).isTrue()
    }

    @Test
    fun `Should return FALSE when SAVE sync operation fails`() = runTest {
        everySuspend { diaryApi.save(any()) } returns Result.Failure(Exception("Error saving to diary api"))

        val result = synchronizer.sync(Synchronizer.SyncOperation.Save(Diary("hello")))
        assertThat(result).isFalse()
    }

    @Test
    fun `Should return TRUE when DELETE sync operation succeeds`() = runTest {
        val result = synchronizer.sync(Synchronizer.SyncOperation.Delete(Diary("hello")))
        assertThat(result).isTrue()
    }

    @Test
    fun `Should return FALSE when DELETE sync operation fails`() = runTest {
        everySuspend { diaryApi.delete(any()) } returns Result.Failure(Exception("Error saving to diary api"))

        val result = synchronizer.sync(Synchronizer.SyncOperation.Delete(Diary("hello")))
        assertThat(result).isFalse()
    }

    @Test
    fun `Should NOT perform any sync operation when there is nothing to sync`() = runTest {
        everySuspend { datasource.getPendingSyncs() } returns emptyList()
        everySuspend { datasource.getPendingDeletes() } returns emptyList()

        // This should trigger initial sync and try to bring the data up to date
        synchronizer.startListening()

        advanceUntilIdle()

        verifySuspend(mode = VerifyMode.not) {
            diaryApi.save(any())
        }

        verifySuspend(mode = VerifyMode.not) {
            diaryApi.delete(any())
        }

        verifySuspend(mode = VerifyMode.not) {
            datasource.delete(any())
        }

        verifySuspend(mode = VerifyMode.not) {
            datasource.update(any())
        }
    }

    @Test
    fun `Should not process new diary entries when synchronizer listener is removed`() = runTest {
        everySuspend { datasource.getPendingSyncs() } returns emptyList()
        everySuspend { datasource.getPendingDeletes() } returns emptyList()

        every { diaryApi.fetchAll() } returns flow {
            emit(listOf(DiaryDto("hello")))
            delay(20)
            emit(listOf(DiaryDto("hello")))
        }

        synchronizer.startListening()
        advanceTimeBy(10)
        synchronizer.stopListening()

        // Should only process a single emission even though there will be two
        verifySuspend(mode = VerifyMode.exactly(1)) {
            datasource.save(diaries = any())
        }
    }
}
