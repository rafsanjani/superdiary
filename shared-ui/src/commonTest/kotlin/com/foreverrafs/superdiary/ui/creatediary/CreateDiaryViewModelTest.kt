package com.foreverrafs.superdiary.ui.creatediary

import com.foreverrafs.superdiary.TestAppDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.diaryai.DiaryAI
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.ui.feature.creatediary.screen.CreateDiaryViewModel
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class CreateDiaryViewModelTest {

    private val diaryAI: DiaryAI = mock<DiaryAI>()

    private val dataSource: DataSource = mock<DataSource>()

    private lateinit var createDiaryViewModel: CreateDiaryViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())

        createDiaryViewModel = CreateDiaryViewModel(
            addDiaryUseCase = AddDiaryUseCase(
                dataSource = dataSource,
                dispatchers = TestAppDispatchers,
                validator = {},
            ),
            diaryAI = diaryAI,
            logger = AggregateLogger(emptyList()),
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should save diary when user clicks on save button`() = runTest {
        val diary = Diary("Hello World!!")
        everySuspend { dataSource.add(diary) }.returns(100L)

        createDiaryViewModel.saveDiary(diary)
        delay(100)

        verifySuspend { dataSource.add(diary) }
    }

    @Test
    fun `Should generate AI diary when generate AI button is clicked`() = runTest {
        every { diaryAI.generateDiary("hello", 100) }.returns(flowOf())

        createDiaryViewModel.generateAIDiary("hello", 100)

        verify { diaryAI.generateDiary(any(), any()) }
    }
}
