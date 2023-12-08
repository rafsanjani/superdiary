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
import com.foreverrafs.superdiary.diary.generator.DiaryAI
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.ui.LocalScreenNavigator
import com.foreverrafs.superdiary.ui.SuperDiaryScreen
import com.foreverrafs.superdiary.ui.feature.creatediary.screen.CreateDiaryScreen
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListScreen
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
            onAddEntry = { navigator.push(CreateDiaryScreen()) },
            onSeeAll = { navigator.push(DiaryListScreen) },
        )
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
    private val diaryAI: DiaryAI,
) :
    StateScreenModel<DashboardScreenModel.DashboardScreenState>(DashboardScreenState.Loading) {
    sealed interface DashboardScreenState {
        data object Loading : DashboardScreenState
        data class Content(
            val latestEntries: List<Diary>,
            val totalEntries: Long,
            val weeklySummary: String?,
        ) : DashboardScreenState
    }

    fun loadDashboardContent() = screenModelScope.launch {
        getAllDiariesUseCase().collect { diaries ->
            if (diaries.isNotEmpty()) generateWeeklySummary(diaries)

            mutableState.update {
                DashboardScreenState.Content(
                    latestEntries = diaries.sortedByDescending { it.date }.take(2),
                    totalEntries = diaries.size.toLong(),
                    weeklySummary = if (diaries.isEmpty()) {
                        "In this panel, your weekly diary entries will be summarized." +
                            "\nAdd your first entry to see how it works"
                    } else {
                        "Generating weekly summary..."
                    },
                )
            }
        }
    }

    private fun generateWeeklySummary(diaries: List<Diary>) = screenModelScope.launch {
        var summary = ""

        diaryAI.generateWeeklySummaryAsync(diaries)
            .catch {
                mutableState.update { state ->
                    (state as? DashboardScreenState.Content)?.copy(
                        weeklySummary = null,
                    ) ?: state
                }
            }
            .collect {
                summary += it

                mutableState.update { state ->
                    (state as? DashboardScreenState.Content)?.copy(
                        weeklySummary = summary,
                    ) ?: state
                }
            }
    }
}
