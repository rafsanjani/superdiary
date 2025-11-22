package com.foreverrafs.superdiary.chat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.ai.domain.model.DiaryChatMessage
import com.foreverrafs.superdiary.ai.domain.model.DiaryChatRole
import com.foreverrafs.superdiary.chat.domain.repository.DiaryChatRepository
import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.domain.model.toDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

sealed interface DiaryChatViewState {
    data object Loading : DiaryChatViewState
    data class Initialized(
        val messages: List<DiaryChatMessage> = emptyList(),
        val errorText: String? = null,
        val isResponding: Boolean = false,
    ) : DiaryChatViewState {
        /**
         * These are messages that can be rendered and displayed onto the UI
         */
        val displayMessages: List<DiaryChatMessage> get() = messages.filter { message -> message.role != DiaryChatRole.System }
    }
}

class DiaryChatViewModel(
    private val logger: AggregateLogger,
    private val repository: DiaryChatRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : ViewModel() {
    private val _viewState = MutableStateFlow<DiaryChatViewState>(DiaryChatViewState.Loading)

    val viewState = _viewState.onStart { initializeContext() }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DiaryChatViewState.Loading,
    )

    private fun initializeContext() = viewModelScope.launch(dispatchers.main) {
        //  Reset chat messages when re-initialising and add the system message
        updateInitializedState { currentState ->
            currentState.copy(
                messages = listOf(
                    DiaryChatMessage.System(
                        content = DiaryAI.QUERY_PROMPT,
                    ),
                ),
            )
        }

        // Observe all the diaries
        repository.getAllDiaries().catch {
            updateInitializedState {
                it.copy(
                    errorText = "Error initializing diaries",
                )
            }
        }.collect { diaries ->
            logger.i(Tag) {
                "Found ${diaries.size} diaries"
            }

            updateInitializedState { currentState ->
                val messages = currentState.messages.toMutableList()

                // Remove the diaries if it already exists in the message chain
                if (messages.size > 1) {
                    logger.i(Tag) {
                        "There are already diaries in the chain, removing the item at position: 1"
                    }
                    messages.removeAt(1)
                }

                /**
                 * Add the diaries exactly as the second element in the message chain. This is essential
                 * because we might persist the chat history, in which case, we always want to replace this
                 * message with the latest diary entries
                 */
                messages.add(
                    index = 1,
                    element = DiaryChatMessage.System(
                        content = Json.encodeToString(
                            value = diaries.map {
                                it.toDto().copy(id = null)
                            },
                        ),
                    ),
                )
                currentState.copy(
                    messages = messages,
                )
            }
        }
    }

    fun queryDiaries(query: String) = viewModelScope.launch(dispatchers.main) {
        // Don't process empty queries
        if (query.isEmpty()) return@launch

        // Add the user's query to the list of messages and render it immediately
        updateInitializedState { currentState ->
            currentState.copy(
                isResponding = true,
                messages = currentState.messages + DiaryChatMessage.User(
                    content = query,
                ),
            )
        }

        val messages = (_viewState.value as? DiaryChatViewState.Initialized)?.messages

        logger.i(Tag) {
            "Querying diaries with ${messages?.size} messages"
        }

        val response = repository.queryDiaries(messages.orEmpty())

        if (response.isNullOrBlank()) {
            logger.e(Tag) {
                "There was an error querying diaries. Empty response returned"
            }

            updateInitializedState {
                it.copy(
                    isResponding = false,
                    errorText = "There was an error querying diaries. Please try again",
                )
            }
            return@launch
        }

        logger.i(Tag) {
            "Successfully added query response to message chain"
        }

        updateInitializedState {
            it.copy(
                isResponding = false,
                messages = it.messages + DiaryChatMessage.DiaryAI(
                    content = response,
                ),
            )
        }
    }

    fun dismissError() = _viewState.update {
        val currentState = it as? DiaryChatViewState.Initialized
        currentState?.copy(
            errorText = null,
        ) ?: it
    }

    private fun updateInitializedState(
        func: (current: DiaryChatViewState.Initialized) -> DiaryChatViewState.Initialized,
    ) {
        _viewState.update { state ->
            val currentState = state as? DiaryChatViewState.Initialized ?: DiaryChatViewState.Initialized()

            val newState = func(currentState)

            logger.d(Tag) {
                "updateInitializedState: Updating content state from ${currentState::class.simpleName} to ${newState::class.simpleName}"
            }

            newState
        }
    }

    companion object {
        val Tag = DiaryChatViewModel::class.simpleName.orEmpty()
    }
}
