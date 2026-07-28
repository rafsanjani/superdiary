package com.foreverrafs.superdiary.insights.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.insights.domain.model.WritingInsightTheme
import com.foreverrafs.superdiary.insights.domain.model.WritingStats
import com.foreverrafs.superdiary.insights.domain.model.toWritingInsightThemes
import com.foreverrafs.superdiary.insights.domain.model.toWritingStats
import com.foreverrafs.superdiary.insights.domain.repository.WritingInsightsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface WritingInsightsViewState {
    data object Loading : WritingInsightsViewState
    data object Empty : WritingInsightsViewState
    data class Error(val error: WritingInsightsError) : WritingInsightsViewState
    data class Content(
        val stats: WritingStats,
        val insights: List<WritingInsightTheme> = emptyList(),
        val isGenerating: Boolean = false,
        val error: WritingInsightsError? = null,
    ) : WritingInsightsViewState
}

enum class WritingInsightsError {
    LoadHistory,
    RefreshInsights,
}

class WritingInsightsViewModel(
    private val logger: AggregateLogger,
    private val repository: WritingInsightsRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : ViewModel() {
    private val mutableViewState =
        MutableStateFlow<WritingInsightsViewState>(WritingInsightsViewState.Loading)
    private var recentEntries: List<Diary> = emptyList()

    val viewState: StateFlow<WritingInsightsViewState> = mutableViewState.asStateFlow()

    init {
        observeEntries()
    }

    private fun observeEntries() = viewModelScope.launch(dispatchers.main) {
        repository.observeEntries()
            .distinctUntilChanged()
            .catch { error ->
                logger.e(TAG, error) { "Unable to load entries for writing insights" }
                mutableViewState.update {
                    WritingInsightsViewState.Error(WritingInsightsError.LoadHistory)
                }
            }
            .collectLatest { entries ->
                recentEntries = entries
                if (entries.isEmpty()) {
                    mutableViewState.update { WritingInsightsViewState.Empty }
                } else {
                    generateInsights(entries)
                }
            }
    }

    fun refresh() {
        if (recentEntries.isEmpty()) {
            observeEntries()
            return
        }

        viewModelScope.launch(dispatchers.main) {
            generateInsights(recentEntries)
        }
    }

    fun dismissError() {
        mutableViewState.update { state ->
            if (state is WritingInsightsViewState.Content) {
                state.copy(error = null)
            } else {
                state
            }
        }
    }

    private suspend fun generateInsights(entries: List<Diary>) {
        val previousInsights =
            (mutableViewState.value as? WritingInsightsViewState.Content)?.insights.orEmpty()

        mutableViewState.update {
            WritingInsightsViewState.Content(
                stats = entries.toWritingStats(),
                insights = previousInsights,
                isGenerating = true,
            )
        }

        val insights = try {
            repository.generateInsights(entries)?.toWritingInsightThemes().orEmpty()
        } catch (error: Exception) {
            logger.e(TAG, error) { "Unable to generate writing insights" }
            emptyList()
        }

        mutableViewState.update { state ->
            val content = state as? WritingInsightsViewState.Content ?: return@update state
            content.copy(
                insights = insights.ifEmpty { content.insights },
                isGenerating = false,
                error = if (insights.isEmpty()) {
                    WritingInsightsError.RefreshInsights
                } else {
                    null
                },
            )
        }
    }

    companion object {
        private const val TAG = "WritingInsightsViewModel"
    }
}
