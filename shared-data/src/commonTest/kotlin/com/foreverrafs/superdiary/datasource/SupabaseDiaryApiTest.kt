package com.foreverrafs.superdiary.datasource

import app.cash.turbine.test
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEmpty
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.datasource.remote.SupabaseDiaryApi
import com.foreverrafs.superdiary.data.model.DiaryDto
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.client.engine.mock.respondOk
import io.ktor.util.reflect.instanceOf
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json

@OptIn(ExperimentalCoroutinesApi::class)
class SupabaseDiaryApiTest {

    private lateinit var supabaseDiaryApi: SupabaseDiaryApi

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(TestAppDispatchers.main)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @OptIn(SupabaseExperimental::class)
    @Test
    fun `Should fetch all diaries successfully`() = runTest {
        val diaryDto = DiaryDto(
            id = 1,
            date = Clock.System.now(),
            entry = "Hello world",
        )

        supabaseDiaryApi = SupabaseDiaryApi(
            supabase = createMockedSupabaseClient(
                requestHandler = {
                    respondOk(
                        listOf(diaryDto).toResponseString(),
                    )
                },
            ),
        )

        supabaseDiaryApi.fetchAll().test {
            val items = awaitItem()
            assertThat(items).isNotEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Should throw exception when fetch all fails`() = runTest {
        supabaseDiaryApi = SupabaseDiaryApi(
            supabase = createMockedSupabaseClient(
                requestHandler = {
                    respondBadRequest()
                },
            ),
        )

        supabaseDiaryApi.fetchAll().test {
            awaitError()
        }
    }

    @Test
    fun `Should return success on delete`() = runTest {
        val diaryDto = DiaryDto(
            id = 1,
            date = Clock.System.now(),
            entry = "Hello world",
        )

        supabaseDiaryApi = SupabaseDiaryApi(
            supabase = createMockedSupabaseClient(
                requestHandler = {
                    respondOk(
                        listOf(diaryDto).toResponseString(),
                    )
                },
            ),
        )

        assertThat(supabaseDiaryApi.delete(diaryDto)).instanceOf(Result.Success::class)
    }

    @Test
    fun `Should return failure on failed delete`() = runTest {
        val diaryDto = DiaryDto(
            id = 1,
            date = Clock.System.now(),
            entry = "Hello world",
        )

        supabaseDiaryApi = SupabaseDiaryApi(
            supabase = createMockedSupabaseClient(
                requestHandler = {
                    respondBadRequest()
                },
            ),
        )

        assertThat(supabaseDiaryApi.delete(diaryDto)).instanceOf(Result.Failure::class)
    }

    @Test
    fun `Should return success on save`() = runTest {
        val diaryDto = DiaryDto(
            id = 1,
            date = Clock.System.now(),
            entry = "Hello world",
        )

        supabaseDiaryApi = SupabaseDiaryApi(
            supabase = createMockedSupabaseClient(
                requestHandler = {
                    respondOk()
                },
            ),
        )

        assertThat(supabaseDiaryApi.save(diaryDto)).instanceOf(Result.Success::class)
    }

    @Test
    fun `Should return failure on failed save`() = runTest {
        val diaryDto = DiaryDto(
            id = 1,
            date = Clock.System.now(),
            entry = "Hello world",
        )

        supabaseDiaryApi = SupabaseDiaryApi(
            supabase = createMockedSupabaseClient(
                requestHandler = {
                    respondBadRequest()
                },
            ),
        )

        assertThat(supabaseDiaryApi.save(diaryDto)).instanceOf(Result.Failure::class)
    }

    @Test
    fun `Should throw CancellationException when save is cancelled`() = runTest {
        val diaryDto = DiaryDto(
            id = 1,
            date = Clock.System.now(),
            entry = "Hello world",
        )

        supabaseDiaryApi = SupabaseDiaryApi(
            supabase = createMockedSupabaseClient(
                requestHandler = {
                    throw CancellationException("Request is cancelled")
                },
            ),
        )

        assertFailure {
            supabaseDiaryApi.save(diaryDto)
        }.isInstanceOf(CancellationException::class)
    }

    @Test
    fun `Should throw CancellationException when delete is cancelled`() = runTest {
        val diaryDto = DiaryDto(
            id = 1,
            date = Clock.System.now(),
            entry = "Hello world",
        )

        supabaseDiaryApi = SupabaseDiaryApi(
            supabase = createMockedSupabaseClient(
                requestHandler = {
                    throw CancellationException("Request is cancelled")
                },
            ),
        )

        assertFailure {
            supabaseDiaryApi.delete(diaryDto)
        }.isInstanceOf(CancellationException::class)
    }

    private fun List<DiaryDto>.toResponseString(): String = Json.encodeToString(this)
}
