//package com.foreverrafs.superdiary.ui.diarylist
//
//import app.cash.turbine.test
//import assertk.assertThat
//import assertk.assertions.isInstanceOf
//import com.foreverrafs.superdiary.diary.datasource.DataSource
//import com.foreverrafs.superdiary.diary.usecase.DeleteMultipleDiariesUseCase
//import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
//import com.foreverrafs.superdiary.diary.usecase.SearchDiaryByDateUseCase
//import com.foreverrafs.superdiary.diary.usecase.SearchDiaryByEntryUseCase
//import com.foreverrafs.superdiary.diary.usecase.UpdateDiaryUseCase
//import com.foreverrafs.superdiary.ui.feature.diarylist.model.DiaryListScreenModel
//import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListScreenState
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.StandardTestDispatcher
//import kotlinx.coroutines.test.resetMain
//import kotlinx.coroutines.test.runTest
//import kotlinx.coroutines.test.setMain
//import kotlin.test.AfterTest
//import kotlin.test.BeforeTest
//import kotlin.test.Test
//
//@OptIn(ExperimentalCoroutinesApi::class)
//class DiaryListScreenModelTest {
//    private val testDispatcher = StandardTestDispatcher()
//
//    private val dataSource: DataSource = TestDataSource()
//    private val getAllDiariesUseCase = GetAllDiariesUseCase(dataSource)
//    private val searchDiaryByEntryUseCase = SearchDiaryByEntryUseCase(dataSource)
//    private val searchDiaryByDateUseCase = SearchDiaryByDateUseCase(dataSource)
//    private val updateDiaryUseCase = UpdateDiaryUseCase(dataSource)
//    private val deleteMultipleDiariesUseCase = DeleteMultipleDiariesUseCase(dataSource)
//
//    private lateinit var diaryListScreenModel: DiaryListScreenModel
//
//    @BeforeTest
//    fun setup() {
//        Dispatchers.setMain(testDispatcher)
//        diaryListScreenModel = DiaryListScreenModel(
//            getAllDiariesUseCase = getAllDiariesUseCase,
//            searchDiaryByEntryUseCase = searchDiaryByEntryUseCase,
//            searchDiaryByDateUseCase = searchDiaryByDateUseCase,
//            updateDiaryUseCase = updateDiaryUseCase,
//            deleteMultipleDiariesUseCase = deleteMultipleDiariesUseCase,
//            coroutineDispatcher = testDispatcher,
//        )
//    }
//
//    @AfterTest
//    fun teardown() {
//        Dispatchers.resetMain()
//    }
//
//    @Test
//    fun `Verify diary list screen starts from loading state`() = runTest {
//        diaryListScreenModel.state.test {
//            diaryListScreenModel.observeDiaries()
//            val state = awaitItem()
//            cancelAndIgnoreRemainingEvents()
//
//            assertThat(state).isInstanceOf<DiaryListScreenState.Loading>()
//        }
//    }
//
//    @Test
//    fun `Verify diary list gets loaded successfully`() = runTest {
//        diaryListScreenModel.state.test {
//            diaryListScreenModel.observeDiaries()
//
//            // Consume and skip loading state
//            skipItems(1)
//            val state = awaitItem()
//            cancelAndIgnoreRemainingEvents()
//
//            assertThat(state).isInstanceOf<DiaryListScreenState.Content>()
//        }
//    }
//}
