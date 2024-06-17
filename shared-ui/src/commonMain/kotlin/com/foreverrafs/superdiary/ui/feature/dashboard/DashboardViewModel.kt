package com.foreverrafs.superdiary.ui.feature.dashboard

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.diaryai.DiaryAI
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.model.Streak
import com.foreverrafs.superdiary.data.model.WeeklySummary
import com.foreverrafs.superdiary.data.usecase.AddWeeklySummaryUseCase
import com.foreverrafs.superdiary.data.usecase.CalculateBestStreakUseCase
import com.foreverrafs.superdiary.data.usecase.CalculateStreakUseCase
import com.foreverrafs.superdiary.data.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.data.usecase.GetWeeklySummaryUseCase
import com.foreverrafs.superdiary.data.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.data.utils.DiaryPreference
import com.foreverrafs.superdiary.data.utils.DiarySettings
import com.foreverrafs.superdiary.data.utils.toDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class DashboardViewModel(
    private val getAllDiariesUseCase: GetAllDiariesUseCase,
    private val calculateStreakUseCase: CalculateStreakUseCase,
    private val calculateBestStreakUseCase: CalculateBestStreakUseCase,
    private val addWeeklySummaryUseCase: AddWeeklySummaryUseCase,
    private val getWeeklySummaryUseCase: GetWeeklySummaryUseCase,
    private val updateDiaryUseCase: UpdateDiaryUseCase,
    private val preference: DiaryPreference,
    private val diaryAI: DiaryAI,
    private val logger: AggregateLogger,
) : StateScreenModel<DashboardViewModel.DashboardScreenState>(DashboardScreenState.Loading) {
    sealed interface DashboardScreenState {
        data object Loading : DashboardScreenState
        data class Content(
            val latestEntries: List<Diary>,
            val totalEntries: Long,
            val weeklySummary: String?,
            val currentStreak: Streak,
            val bestStreak: Streak,
        ) : DashboardScreenState
    }

    val settings: Flow<DiarySettings> get() = preference.settings

    fun loadDashboardContent() = screenModelScope.launch {
        logger.i(Tag) {
            "Loading dashboard content"
        }

        getAllDiariesUseCase()
            .collect { diaries ->
                logger.i(Tag) {
                    "Dashboard content refreshed!"
                }
                mutableState.update {
                    DashboardScreenState.Content(
                        latestEntries = diaries.sortedByDescending { it.date }.take(4),
                        totalEntries = diaries.size.toLong(),
                        weeklySummary = if (diaries.isEmpty()) {
                            """
                            In this panel, your weekly diary entries will be summarized.
                            Add your first entry to see how it works
                            """.trimIndent()
                        } else {
                            DEFAULT_SUMMARY_TEXT
                        },
                        currentStreak = Streak(
                            0,
                            Clock.System.now().toDate(),
                            Clock.System.now().toDate(),
                        ),
                        bestStreak = Streak(
                            0,
                            Clock.System.now().toDate(),
                            Clock.System.now().toDate(),
                        ),
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
                val newState = func(currentState)

                logger.d(Tag) {
                    "updateContentState: Updating content state"
                }

                newState
            } else {
                logger.i(Tag) {
                    "Current state is null, cannot update"
                }
                state
            }
        }
    }

    private fun generateWeeklySummary(diaries: List<Diary>) = screenModelScope.launch {
        logger.i(Tag) {
            "generateWeeklySummary: Fetching weekly summary for ${diaries.size} entries"
        }
        val latestWeeklySummary = getWeeklySummaryUseCase()

        latestWeeklySummary?.let {
            val difference = Clock.System.now() - latestWeeklySummary.date

            if (difference.inWholeDays <= 7L) {
                logger.i(Tag) {
                    "generateWeeklySummary: Weekly summary was generated ${difference.inWholeDays} days ago." +
                        " Skip generation for now"
                }
                updateContentState { currentState ->
                    currentState.copy(weeklySummary = latestWeeklySummary.summary)
                }
                return@launch
            }
        }

        diaryAI.getWeeklySummary(diaries)
            .catch { exception ->
                logger.e(Tag, exception) {
                    "generateWeeklySummary: An error occurred generating weekly summary"
                }
            }.onCompletion {
                (mutableState.value as? DashboardScreenState.Content)?.let { appState ->
                    if (appState.weeklySummary == DEFAULT_SUMMARY_TEXT) {
                        updateContentState { currentState ->
                            currentState.copy(weeklySummary = "Error generating weekly summary")
                        }
                        return@onCompletion
                    }

                    logger.d(Tag) {
                        "generateWeeklySummary: Weekly summary generated!"
                    }
                    appState.weeklySummary?.let { summary ->
                        addWeeklySummaryUseCase(
                            weeklySummary = WeeklySummary(summary),
                        )
                    }
                }
            }.collect { chunk ->
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
        logger.i(Tag) {
            "calculateStreak: Calculating streak for ${diaries.size} entries"
        }
        val streak = calculateStreakUseCase(diaries)
        val bestStreak = calculateBestStreakUseCase(diaries)

        logger.i(Tag) {
            "calculateStreak: Streak: ${streak.count}\nBest Streak: ${bestStreak.count}"
        }

        mutableState.update { state ->
            (state as? DashboardScreenState.Content)?.copy(
                currentStreak = streak,
                bestStreak = bestStreak,
            ) ?: state
        }
    }

    suspend fun toggleFavorite(diary: Diary): Boolean {
        val result = updateDiaryUseCase(
            diary.copy(isFavorite = !diary.isFavorite),
        )

        return when (result) {
            is Result.Failure -> {
                logger.e(Tag, result.error) {
                    "toggleFavorite: Error adding/removing favorite for diary ${diary.id}"
                }
                false
            }

            is Result.Success -> {
                logger.d(Tag) {
                    val message = if (diary.isFavorite) {
                        "toggleFavorite: Successfully added diary: ${diary.id} to favorites"
                    } else {
                        "toggleFavorite: Successfully removed diary: ${diary.id} from favorites"
                    }

                    message
                }
                result.data
            }
        }
    }

    fun updateSettings(settings: DiarySettings) = screenModelScope.launch {
        logger.i(Tag) {
            "updateSettings: Updating settings from ${preference.snapshot} with values $settings"
        }
        preference.save(settings)
    }

    companion object {
        private const val DEFAULT_SUMMARY_TEXT = "Generating weekly Summary..."
        private val Tag = DashboardViewModel::class.simpleName.orEmpty()
    }
}
