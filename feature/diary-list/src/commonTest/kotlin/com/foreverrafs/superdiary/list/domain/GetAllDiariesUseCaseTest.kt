package com.foreverrafs.superdiary.list.domain

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.messageContains
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.list.domain.repository.DiaryListRepository
import com.foreverrafs.superdiary.list.domain.usecase.GetAllDiariesUseCase
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class GetAllDiariesUseCaseTest {

    private val repository: DiaryListRepository = mock()
    private val getAllDiariesUseCase = GetAllDiariesUseCase(repository)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(TestAppDispatchers.main)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Getting all diaries returns repository diaries`() = runTest {
        val diaries = listOf(
            Diary(id = 1L, entry = "First diary"),
            Diary(id = 2L, entry = "Second diary"),
        )
        every { repository.getAllDiaries() }.returns(flowOf(PagingData.from(diaries)))

        val result = getAllDiariesUseCase().asSnapshot()

        assertThat(result).isEqualTo(diaries)
    }

    @Test
    fun `Getting all diaries propagates repository errors`() = runTest {
        every { repository.getAllDiaries() }.returns(
            flow { throw IllegalStateException("Unable to load diaries") },
        )

        assertFailure {
            getAllDiariesUseCase().asSnapshot()
        }.isInstanceOf<IllegalStateException>()
            .messageContains("Unable to load diaries")

        verify(mode = VerifyMode.exactly(1)) {
            repository.getAllDiaries()
        }
    }
}
