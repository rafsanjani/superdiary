package com.foreverrafs.superdiary.chat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.ai.domain.model.DiaryChatMessage
import com.foreverrafs.superdiary.ai.domain.model.DiaryChatRole
import com.foreverrafs.superdiary.chat.domain.repository.DiaryChatRepository
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.domain.model.toDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

sealed interface DiaryChatViewState {
    data object Loading : DiaryChatViewState
    data class Initialized(
        val model: DiaryChatUiModel,
    ) : DiaryChatViewState
}

data class DiaryChatUiModel(
    val messages: List<DiaryChatMessage> = emptyList(),
    val errorText: String? = null,
    val isResponding: Boolean = false,
)

class DiaryChatViewModel(
    private val logger: AggregateLogger,
    private val repository: DiaryChatRepository,
) : ViewModel() {
    private val _viewState = MutableStateFlow<DiaryChatViewState>(DiaryChatViewState.Loading)

    private val messages: MutableList<DiaryChatMessage> = mutableListOf()
    val viewState = _viewState
        .onStart { initializeContext() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DiaryChatViewState.Loading,
        )

    private fun initializeContext() = viewModelScope.launch {
        // Add the system message
        messages.add(
            DiaryChatMessage.System(
                content = DiaryAI.QUERY_PROMPT,
            ),
        )

        // Observe all the diaries
        repository.getAllDiaries().collect { diaries ->
            _viewState.update {
                DiaryChatViewState.Initialized(
                    model = DiaryChatUiModel(),
                )
            }
            logger.i(Tag) {
                "Found ${diaries.size} diaries"
            }

            val json = Json.encodeToString(
                value = diaries.map {
                    it.toDto().copy(id = null)
                },
            )

            // Remove the diaries if it already exists in the message chain
            if (messages.size > 1) {
                logger.i(Tag) {
                    "There are already diaries in the chain, removing the item at position: 1"
                }
                messages.removeAt(1)
            }

            // Add the diaries exactly as the second element in the messages
            messages.add(
                index = 1,
                element = DiaryChatMessage.System(
                    content = json,
                ),
            )
        }
    }

    fun queryDiaries(query: String) = viewModelScope.launch {
        val currentState = _viewState.value as? DiaryChatViewState.Initialized
        val userMessage = DiaryChatMessage.User(
            content = query,
        )

        messages.add(userMessage)

        _viewState.update {
            currentState?.copy(
                model = currentState.model.copy(
                    isResponding = true,
                    messages = filterMessages(),
                ),
            ) ?: it
        }

        val response = repository.queryDiaries(messages)

        if (response.isNullOrBlank()) {
            logger.e(Tag) {
                "There was an error querying diaries. Empty response returned"
            }

            _viewState.update {
                currentState?.copy(
                    model = currentState.model.copy(
                        isResponding = false,
                        errorText = "There was an error querying diaries. Please try again",
                    ),
                ) ?: it
            }
            return@launch
        }

        messages.add(
            DiaryChatMessage.DiaryAI(
                content = response,
            ),
        )

        logger.i(Tag) {
            "Successfully added query response to message chain. Items: ${messages.size}"
        }

        _viewState.update {
            currentState?.copy(
                model = currentState.model.copy(
                    isResponding = false,
                    messages = filterMessages(),
                ),
            ) ?: it
        }
    }

    private fun filterMessages(): List<DiaryChatMessage> = messages.filter { message ->
        message.role == DiaryChatRole.User || message.role == DiaryChatRole.DiaryAI
    }

    companion object {
        val Tag = DiaryChatViewModel::class.simpleName.orEmpty()
    }
}
