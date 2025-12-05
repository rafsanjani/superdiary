package com.foreverrafs.superdiary.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.auth.BiometricAuth
import com.foreverrafs.preferences.DiaryPreference
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.dashboard.domain.CountEntriesUseCase
import com.foreverrafs.superdiary.dashboard.domain.GenerateWeeklySummaryUseCase
import com.foreverrafs.superdiary.dashboard.domain.GetRecentEntriesUseCase
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.getOrElse
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.Streak
import com.foreverrafs.superdiary.domain.usecase.CalculateBestStreakUseCase
import com.foreverrafs.superdiary.domain.usecase.CalculateStreakUseCase
import com.foreverrafs.superdiary.domain.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.utils.DiarySettings
import com.foreverrafs.superdiary.utils.toDate
import kotlin.time.Clock
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Suppress("LongParameterList")
class DashboardViewModel(
    private val calculateStreakUseCase: CalculateStreakUseCase,
    private val getRecentEntriesUseCase: GetRecentEntriesUseCase,
    private val countEntriesUseCase: CountEntriesUseCase,
    private val calculateBestStreakUseCase: CalculateBestStreakUseCase,
    private val updateDiaryUseCase: UpdateDiaryUseCase,
    private val preference: DiaryPreference,
    private val biometricAuth: BiometricAuth,
    private val generateWeeklySummaryUseCase: GenerateWeeklySummaryUseCase,
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

    val state: StateFlow<DashboardScreenState> = mutableState.onStart {
        loadDashboardContent()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = DashboardScreenState.Loading,
    )

    private fun loadDashboardContent() = viewModelScope.launch {
        // Transition to loading state if it isn't already there
        mutableState.update {
            it as? DashboardScreenState.Loading ?: DashboardScreenState.Loading
        }

        logger.i(TAG) {
            "Loading dashboard content"
        }

        val latestEntriesDeferred: Deferred<Result<List<Diary>>> =
            async { getRecentEntriesUseCase(count = 5) }
        val countEntriesDeferred: Deferred<Result<Long>> = async { countEntriesUseCase() }

        preference.settings.collect { settings ->
            val state = when (val result = latestEntriesDeferred.await()) {
                is Result.Failure -> {
                    logger.e(TAG, result.error) {
                        "Error loading dashboard contents"
                    }

                    DashboardScreenState.Error(
                        message = result.error.message.orEmpty()
                    )
                }

                is Result.Success -> {
                    val diaries = result.data

                    logger.i(TAG) {
                        "Dashboard content refreshed!"
                    }

                    val shouldShowBiometricDialog =
                        settings.showBiometricAuthDialog && biometricAuth.canAuthenticate() && !settings.isBiometricAuthEnabled

                    if (diaries.isNotEmpty()) {
                        generateWeeklySummary(diaries)
                        calculateStreak(diaries)
                    }

                    DashboardScreenState.Content(
                        latestEntries = diaries.take(4),
                        totalEntries = countEntriesDeferred.await().getOrElse { 0L },
                        weeklySummary = if (diaries.isEmpty()) {
                            """

                            """.trimIndent()
                        } else {
                            DEFAULT_SUMMARY_TEXT
                        },
                        currentStreak = Streak(
                            count = 0,
                            startDate = clock.now().toDate(),
                            endDate = clock.now().toDate(),
                        ),
                        bestStreak = Streak(
                            count = 0,
                            startDate = clock.now().toDate(),
                            endDate = clock.now().toDate(),
                        ),
                        showBiometricAuthDialog = shouldShowBiometricDialog,
                        showLatestEntries = settings.showLatestEntries,
                        showAtaGlance = settings.showAtAGlance,
                        showWeeklySummary = settings.showWeeklySummary,
                    )
                }
            }

            mutableState.update { state }
        }
    }

    fun onRetry() = loadDashboardContent()

    /**
     * Only use this function if you are sure the current state of the screen
     * is [DashboardScreenState.Content]
     */
    private fun updateContentState(func: (current: DashboardScreenState.Content) -> DashboardScreenState.Content) {
        mutableState.update { state ->
            val currentState =
                state as? DashboardScreenState.Content ?: DashboardScreenState.Content()

            val newState = func(currentState)

            logger.d(TAG) {
                "updateContentState: Updating content state from ${currentState::class.simpleName} to ${newState::class.simpleName}"
            }

            newState
        }
    }

    private fun generateWeeklySummary(diaries: List<Diary>) = viewModelScope.launch {
        generateWeeklySummaryUseCase(diaries).collect { summary ->
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
        initializeBiometricAuth()
    }

    private suspend fun initializeBiometricAuth() {
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
