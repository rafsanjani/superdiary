//package com.foreverrafs.superdiary.ui.diarychat
//
//import app.cash.turbine.test
//import assertk.assertThat
//import assertk.assertions.isTrue
//import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
//import com.foreverrafs.superdiary.ui.feature.diarychat.DiaryChatScreenModel
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
//class DiaryChatScreenModelTest {
//    private val coroutineDispatcher = StandardTestDispatcher()
//    private val screenModel =
//        DiaryChatScreenModel(
//            diaryAI = TestDiaryAI(),
//            getAllDiariesUseCase = GetAllDiariesUseCase(TestDataSource()),
//        )
//
//    @BeforeTest
//    fun setup() {
//        Dispatchers.setMain(coroutineDispatcher)
//    }
//
//    @AfterTest
//    fun teardown() {
//        Dispatchers.resetMain()
//    }
//
//    @Test
//    fun `Update responding state when responding to AI diary queries`() = runTest {
//        screenModel.state.test {
//            screenModel.queryDiaries("hello World")
//
//            // Skip the initial state
//            skipItems(1)
//            val state = awaitItem()
//
//            cancelAndConsumeRemainingEvents()
//
//            assertThat(state.isResponding).isTrue()
//        }
//    }
//}
