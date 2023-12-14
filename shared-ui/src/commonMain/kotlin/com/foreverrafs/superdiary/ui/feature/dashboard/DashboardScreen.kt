package com.foreverrafs.superdiary.ui.feature.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.foreverrafs.superdiary.diary.diaryai.DiaryAI
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.model.Streak
import com.foreverrafs.superdiary.diary.model.WeeklySummary
import com.foreverrafs.superdiary.diary.usecase.AddWeeklySummaryUseCase
import com.foreverrafs.superdiary.diary.usecase.CalculateStreakUseCase
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.GetWeeklySummaryUseCase
import com.foreverrafs.superdiary.ui.LocalScreenNavigator
import com.foreverrafs.superdiary.ui.SuperDiaryScreen
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListScreen
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

object DashboardScreen : SuperDiaryScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalScreenNavigator.current

        val screenModel: DashboardScreenModel = getScreenModel()
        val screenState by screenModel.state.collectAsState()

        LaunchedEffect(Unit) {
            screenModel.loadDashboardContent()
        }

        DashboardScreenContent(
            state = screenState,
        ) { navigator.push(DiaryListScreen) }
    }

    override val selectedIcon: VectorPainter
        @Composable
        get() = rememberVectorPainter(Icons.Filled.StackedBarChart)

    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 0u,
            title = "Dashboard",
            icon = rememberVectorPainter(Icons.Outlined.BarChart),
        )
}

class DashboardScreenModel(
    private val getAllDiariesUseCase: GetAllDiariesUseCase,
    private val calculateStreakUseCase: CalculateStreakUseCase,
    private val addWeeklySummaryUseCase: AddWeeklySummaryUseCase,
    private val getWeeklySummaryUseCase: GetWeeklySummaryUseCase,
    private val diaryAI: DiaryAI,
) :
    StateScreenModel<DashboardScreenModel.DashboardScreenState>(DashboardScreenState.Loading) {
    sealed interface DashboardScreenState {
        data object Loading : DashboardScreenState
        data class Content(
            val latestEntries: List<Diary>,
            val totalEntries: Long,
            val weeklySummary: String?,
            val streak: Streak,
        ) : DashboardScreenState
    }

    fun loadDashboardContent() = screenModelScope.launch {
        getAllDiariesUseCase().collect { diaries ->
            mutableState.update {
                DashboardScreenState.Content(
                    latestEntries = diaries.sortedByDescending { it.date }.take(2),
                    totalEntries = diaries.size.toLong(),
                    weeklySummary = if (diaries.isEmpty()) {
                        "In this panel, your weekly diary entries will be summarized." +
                            "\nAdd your first entry to see how it works"
                    } else {
                        DEFAULT_SUMMARY_TEXT
                    },
                    streak = Streak(0, emptyList()),
                )
            }

            if (diaries.isNotEmpty()) {
                generateWeeklySummary(diaries)
                calculateStreak(diaries)
            }
        }
    }

    private fun updateContentState(func: (current: DashboardScreenState.Content) -> DashboardScreenState.Content) {
        mutableState.update { state ->
            val currentState = state as? DashboardScreenState.Content
            if (currentState != null) {
                func(currentState)
            } else {
                state
            }
        }
    }

    private fun generateWeeklySummary(diaries: List<Diary>) = screenModelScope.launch {
        val latestWeeklySummary = getWeeklySummaryUseCase()

        latestWeeklySummary?.let {
            val difference = Clock.System.now() - latestWeeklySummary.date

            if (difference.inWholeDays <= 7L) {
                updateContentState { currentState ->
                    currentState.copy(weeklySummary = latestWeeklySummary.summary)
                }
                return@launch
            }
        }

        diaryAI.generateWeeklySummaryAsync(diaries)
            .catch {
                mutableState.update { state ->
                    (state as? DashboardScreenState.Content)?.copy(
                        weeklySummary = null,
                    ) ?: state
                }
            }
            .onCompletion { exception ->
                (mutableState.value as? DashboardScreenState.Content)?.let {
                    if (exception == null) {
                        addWeeklySummaryUseCase(
                            WeeklySummary(
                                summary = it.weeklySummary.orEmpty(),
                                date = Clock.System.now(),
                            ),
                        )
                    }
                }
            }
            .collect { chunk ->
                updateContentState { state ->
                    state.copy(
                        weeklySummary = if (state.weeklySummary == DEFAULT_SUMMARY_TEXT) {
                            chunk
                        } else {
                            state.weeklySummary + chunk
                        },
                    )
                }
            }
    }

    private fun calculateStreak(diaries: List<Diary>) = screenModelScope.launch {
        val streak = calculateStreakUseCase(diaries)

        mutableState.update { state ->
            (state as? DashboardScreenState.Content)?.copy(
                streak = streak,
            ) ?: state
        }
    }

    companion object {
        private const val DEFAULT_SUMMARY_TEXT = "Generating weekly Summary..."
    }
}
