package com.foreverrafs.superdiary.ui.diarychat

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isTrue
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.diaryai.DiaryAI
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.ui.feature.diarychat.DiaryChatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.kodein.mock.Mock
import org.kodein.mock.tests.TestsWithMocks
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DiaryChatViewModelTest : TestsWithMocks() {
    override fun setUpMocks() = injectMocks(mocker)

    @Mock
    lateinit var diaryAI: DiaryAI

    @Mock
    lateinit var dataSource: DataSource

    private lateinit var diaryChatViewModel: DiaryChatViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())

        every { dataSource.fetchAll() } returns flowOf(emptyList())

        diaryChatViewModel = DiaryChatViewModel(
            diaryAI = diaryAI,
            getAllDiariesUseCase = GetAllDiariesUseCase(dataSource),
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Update responding state when responding to AI diary queries`() = runTest {
        everySuspending { diaryAI.queryDiaries(isAny(), isAny()) } returns ""

        diaryChatViewModel.state.test {
            diaryChatViewModel.queryDiaries("hello World")

            // Skip the initial state
            skipItems(1)
            val state = awaitItem()

            cancelAndConsumeRemainingEvents()

            assertThat(state.isResponding).isTrue()
        }
    }
}
