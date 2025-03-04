package com.foreverrafs.superdiary.ui.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.auth.BiometricAuth
import com.foreverrafs.preferences.DiaryPreference
import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.Streak
import com.foreverrafs.superdiary.domain.model.WeeklySummary
import com.foreverrafs.superdiary.domain.usecase.AddWeeklySummaryUseCase
import com.foreverrafs.superdiary.domain.usecase.CalculateBestStreakUseCase
import com.foreverrafs.superdiary.domain.usecase.CalculateStreakUseCase
import com.foreverrafs.superdiary.domain.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.domain.usecase.GetWeeklySummaryUseCase
import com.foreverrafs.superdiary.domain.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.utils.DiarySettings
import com.foreverrafs.superdiary.utils.toDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

@Suppress("LongParameterList")
class DashboardViewModel(
    getAllDiariesUseCase: GetAllDiariesUseCase,
    private val calculateStreakUseCase: CalculateStreakUseCase,
    private val calculateBestStreakUseCase: CalculateBestStreakUseCase,
    private val addWeeklySummaryUseCase: AddWeeklySummaryUseCase,
    private val getWeeklySummaryUseCase: GetWeeklySummaryUseCase,
    private val updateDiaryUseCase: UpdateDiaryUseCase,
    private val preference: DiaryPreference,
    private val diaryAI: DiaryAI,
    private val biometricAuth: BiometricAuth,
    private val logger: AggregateLogger,
    private val clock: Clock,
) : ViewModel() {
    sealed interface DashboardScreenState {
        data object Loading : DashboardScreenState
        data class Error(val message: String) : DashboardScreenState
        data class Content(
            val latestEntries: List<Diary> = emptyList(),
            val totalEntries: Long = 0L,
            val weeklySummary: String? = null,
            val currentStreak: Streak? = null,
            val bestStreak: Streak? = null,
            val showBiometricAuthDialog: Boolean? = null,
            val showWeeklySummary: Boolean? = null,
            val showLatestEntries: Boolean? = null,
            val showAtaGlance: Boolean? = null,
            val isBiometricAuthError: Boolean? = null,
        ) : DashboardScreenState
    }

    private val mutableState = MutableStateFlow<DashboardScreenState>(DashboardScreenState.Loading)

    private val getAllDiariesResult: Flow<Result<List<Diary>>> = getAllDiariesUseCase()

    val state: StateFlow<DashboardScreenState> = mutableState
        .onStart {
            viewModelScope.launch {
                loadDashboardContent()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = DashboardScreenState.Loading,
        )

    private fun observeSettingsChanges() = viewModelScope.launch {
        preference.settings.collect { settings ->
            logger.i(TAG) {
                settings.toString()
            }
            updateContentState {
                it.copy(
                    showBiometricAuthDialog = settings.showBiometricAuthDialog && !settings.isBiometricAuthEnabled,
                    showWeeklySummary = settings.showWeeklySummary,
                    showLatestEntries = settings.showLatestEntries,
                    showAtaGlance = settings.showAtAGlance,
                )
            }

            // Flip initial launch switch if it is still active
            if (settings.isFirstLaunch) {
                preference.save { it.copy(isFirstLaunch = false) }
            }
        }
    }

    private fun loadDashboardContent() = viewModelScope.launch {
        delay(2000)
        logger.i(TAG) {
            "Loading dashboard content"
        }

        getAllDiariesResult.combine(preference.settings) { result, settings ->
            when (result) {
                is Result.Failure ->
                    DashboardScreenState.Error(
                        message = result.error.message.orEmpty(),
                    )

                is Result.Success -> {
                    val diaries = result.data

                    logger.i(TAG) {
                        "Dashboard content refreshed!"
                    }

                    val shouldShowBiometricDialog =
                        settings.showBiometricAuthDialog &&
                            biometricAuth.canAuthenticate() &&
                            !settings.isBiometricAuthEnabled

                    DashboardScreenState.Content(
                        latestEntries = diaries.sortedByDescending { it.date }.take(4),
                        totalEntries = diaries.size.toLong(),
                        weeklySummary = if (diaries.isEmpty()) {
                            """

                            """.trimIndent()
                        } else {
                            DEFAULT_SUMMARY_TEXT
                        },
                        currentStreak = Streak(
                            0,
                            clock.now().toDate(),
                            clock.now().toDate(),
                        ),
                        bestStreak = Streak(
                            0,
                            clock.now().toDate(),
                            clock.now().toDate(),
                        ),
                        showBiometricAuthDialog = shouldShowBiometricDialog,
                        showLatestEntries = settings.showLatestEntries,
                        showAtaGlance = settings.showAtAGlance,
                        showWeeklySummary = settings.showWeeklySummary,
                    )
                }
            }
        }.collect { updatedState ->
            mutableState.update {
                updatedState
            }
        }

        getAllDiariesResult.collect { result ->
            when (result) {
                is Result.Failure -> mutableState.update {
                    DashboardScreenState.Error(
                        message = result.error.message.orEmpty(),
                    )
                }

                is Result.Success -> {
                    val diaries = result.data

                    logger.i(TAG) {
                        "Dashboard content refreshed!"
                    }

                    mutableState.update {
                        val settings = preference.getSnapshot()

                        val shouldShowBiometricDialog =
                            settings.showBiometricAuthDialog &&
                                biometricAuth.canAuthenticate() &&
                                !settings.isBiometricAuthEnabled

                        DashboardScreenState.Content(
                            latestEntries = diaries.sortedByDescending { it.date }.take(4),
                            totalEntries = diaries.size.toLong(),
                            weeklySummary = if (diaries.isEmpty()) {
                                """

                                """.trimIndent()
                            } else {
                                DEFAULT_SUMMARY_TEXT
                            },
                            currentStreak = Streak(
                                0,
                                clock.now().toDate(),
                                clock.now().toDate(),
                            ),
                            bestStreak = Streak(
                                0,
                                clock.now().toDate(),
                                clock.now().toDate(),
                            ),
                            showBiometricAuthDialog = shouldShowBiometricDialog,
                        )
                    }

                    if (diaries.isNotEmpty()) {
                        generateWeeklySummary(diaries)
                        calculateStreak(diaries)
                    }
                }
            }
        }
    }

    /**
     * Only use this function if you are sure the current state of the screen
     * is [DashboardScreenState.Content]
     */
    private fun updateContentState(func: (current: DashboardScreenState.Content) -> DashboardScreenState.Content) {
        mutableState.update { state ->
            val currentState = state as? DashboardScreenState.Content

            if (currentState != null) {
                val newState = func(currentState)

                logger.d(TAG) {
                    "updateContentState: Updating content state from $currentState to $newState"
                }

                newState
            } else {
                logger.i(TAG) {
                    "Current state is null, cannot update"
                }
                state
            }
        }
    }

    private fun generateWeeklySummary(diaries: List<Diary>) = viewModelScope.launch {
        logger.i(TAG) {
            "generateWeeklySummary: Fetching weekly summary for ${diaries.size} entries"
        }
        val latestWeeklySummary = getWeeklySummaryUseCase()

        latestWeeklySummary?.let {
            val difference = clock.now() - latestWeeklySummary.date

            if (difference.inWholeDays <= 7L) {
                logger.i(TAG) {
                    "generateWeeklySummary: Weekly summary was generated ${difference.inWholeDays} days ago." +
                        " Skip generation for now"
                }
                updateContentState { currentState ->
                    currentState.copy(weeklySummary = latestWeeklySummary.summary)
                }
                return@launch
            }
        }

        diaryAI.generateSummary(diaries)
            .catch { exception ->
                logger.e(TAG, exception) {
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

                    logger.d(TAG) {
                        "generateWeeklySummary: Weekly summary generated!"
                    }
                    appState.weeklySummary?.let { summary ->
                        addWeeklySummaryUseCase(
                            weeklySummary = WeeklySummary(summary),
                        )
                    }
                }
            }
            .collect { summary ->
                updateContentState { state ->
                    state.copy(
                        weeklySummary = summary,
                    )
                }
            }
    }

    private fun calculateStreak(diaries: List<Diary>) = viewModelScope.launch {
        logger.i(TAG) {
            "calculateStreak: Calculating streak for ${diaries.size} entries"
        }
        val streak = calculateStreakUseCase(diaries)
        val bestStreak = calculateBestStreakUseCase(diaries)

        logger.i(TAG) {
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
                logger.e(TAG, result.error) {
                    "toggleFavorite: Error adding/removing favorite for diary ${diary.id}"
                }
                false
            }

            is Result.Success -> {
                logger.d(TAG) {
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

    fun onEnableBiometricAuth() = viewModelScope.launch {
        enableBiometricAuth()
    }

    private suspend fun enableBiometricAuth() {
        if (!biometricAuth.canAuthenticate()) {
            logger.i(TAG) {
                "Biometric authentication is not available"
            }

            updateContentState {
                it.copy(
                    isBiometricAuthError = true,
                    showBiometricAuthDialog = false,
                )
            }

            return
        }

        when (val biometricAuthResult = biometricAuth.startBiometricAuth()) {
            is BiometricAuth.AuthResult.Error -> {
                logger.e(
                    tag = TAG,
                    throwable = biometricAuthResult.error,
                ) {
                    "Error performing biometric authentication"
                }
                updateContentState {
                    it.copy(
                        isBiometricAuthError = true,
                        showBiometricAuthDialog = false,
                    )
                }
            }

            is BiometricAuth.AuthResult.Failed -> updateContentState {
                it.copy(
                    isBiometricAuthError = true,
                    showBiometricAuthDialog = false,
                )
            }

            is BiometricAuth.AuthResult.Success -> {
                preference.save {
                    it.copy(
                        isBiometricAuthEnabled = true,
                        showBiometricAuthDialog = false,
                    )
                }
            }
        }
    }

    fun onUpdateSettings(block: (DiarySettings) -> DiarySettings) = viewModelScope.launch {
        val settings = preference.getSnapshot()
        val updatedSettings = block(settings)

        preference.save {
            updatedSettings
        }
    }

    companion object {
        private const val DEFAULT_SUMMARY_TEXT = "Generating weekly Summary..."
        private const val TAG = "DashboardViewModel"
    }
}
