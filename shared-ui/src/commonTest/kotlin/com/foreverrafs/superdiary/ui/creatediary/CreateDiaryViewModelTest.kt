package com.foreverrafs.superdiary.ui.creatediary

import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.diaryai.DiaryAI
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.ui.feature.creatediary.screen.CreateDiaryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
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
class CreateDiaryViewModelTest : TestsWithMocks() {
    override fun setUpMocks() = injectMocks(mocker)
    private val dispatcher = StandardTestDispatcher()

    @Mock
    lateinit var diaryAI: DiaryAI

    @Mock
    lateinit var dataSource: DataSource

    private lateinit var createDiaryViewModel: CreateDiaryViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)

        createDiaryViewModel = CreateDiaryViewModel(
            addDiaryUseCase = AddDiaryUseCase(dataSource) {},
            diaryAI = diaryAI,
            coroutineDispatcher = dispatcher,
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should save diary when user clicks on save button`() = runTest {
        val diary = Diary("Hello World!!")
        everySuspending { dataSource.add(diary) } returns 100L

        runBlocking {
            createDiaryViewModel.saveDiary(diary)
        }

        // The monotonic clock seems out of sync with MockMP so put in a superficial
        // delay here to prevent verifications from happening before dataSource is
        // engaged
        delay(1)

        verifyWithSuspend {
            dataSource.add(diary)
        }
    }

    @Test
    fun `Should generate AI diary when generate AI button is clicked`() = runTest {
        every { diaryAI.generateDiary(isAny(), isAny()) } returns flowOf()

        createDiaryViewModel.generateAIDiary("", 100)

        verify { diaryAI.generateDiary(isAny(), isAny()) }
    }
}
