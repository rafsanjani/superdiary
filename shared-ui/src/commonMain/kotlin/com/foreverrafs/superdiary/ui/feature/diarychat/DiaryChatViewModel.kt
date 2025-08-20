package com.foreverrafs.superdiary.ui.feature.diarychat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.ai.domain.model.DiaryChatMessage
import com.foreverrafs.superdiary.ai.domain.model.DiaryChatRole
import com.foreverrafs.superdiary.ai.domain.usecase.GetChatMessagesUseCase
import com.foreverrafs.superdiary.ai.domain.usecase.SaveChatMessageUseCase
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.toDto
import com.foreverrafs.superdiary.domain.usecase.GetAllDiariesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class DiaryChatViewModel(
    private val diaryAI: DiaryAI,
    getAllDiariesUseCase: GetAllDiariesUseCase,
    private val logger: AggregateLogger,
    private val saveChatMessageUseCase: SaveChatMessageUseCase,
    getChatMessagesUseCase: GetChatMessagesUseCase,
) : ViewModel() {

    data class DiaryChatViewState(
        val isResponding: Boolean = false,
        val isLoadingDiaries: Boolean = false,
        val messages: List<DiaryChatMessage> = emptyList(),
        val diaries: List<Diary> = emptyList(),
        val error: String? = null,
    )

    private val _viewState = MutableStateFlow(DiaryChatViewState())
    val viewState: StateFlow<DiaryChatViewState> = _viewState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DiaryChatViewState(),
    )

    private val diariesFlow: Flow<Result<List<Diary>>> = getAllDiariesUseCase()
    private val chatMessagesFlow: Flow<List<DiaryChatMessage>> = getChatMessagesUseCase()

    init {
        initializeData()
    }

    private fun initializeData() {
        // Collect diaries
        viewModelScope.launch {
            _viewState.update { it.copy(isLoadingDiaries = true, error = null) }

            try {
                diariesFlow.collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _viewState.update {
                                it.copy(
                                    diaries = result.data,
                                    isLoadingDiaries = false,
                                    error = null,
                                )
                            }
                            logger.i(TAG) { "Loaded diaries for chat screen: Size = ${result.data.size}" }
                        }

                        is Result.Failure -> {
                            _viewState.update {
                                it.copy(
                                    isLoadingDiaries = false,
                                    error = "Failed to load diaries",
                                )
                            }
                            logger.e(TAG) { "Error loading diaries: ${result.error.message}" }
                        }
                    }
                }
            } catch (e: Exception) {
                _viewState.update {
                    it.copy(
                        isLoadingDiaries = false,
                        error = "Unexpected error loading diaries",
                    )
                }
                logger.e(TAG) { "Unexpected error in diaries collection: $e" }
            }
        }

        // Collect chat messages
        viewModelScope.launch {
            try {
                chatMessagesFlow.collect { messages ->
                    val currentState = _viewState.value
                    val updatedMessages = messages.ifEmpty {
                        listOf(welcomeMessage())
                    }

                    val finalMessages =
                        updateSystemMessageInList(updatedMessages, currentState.diaries)
                    _viewState.update { it.copy(messages = finalMessages) }
                }
            } catch (e: Exception) {
                logger.e(TAG) { "Error collecting chat messages: $e" }
                _viewState.update { it.copy(error = "Failed to load chat messages") }
            }
        }
    }

    private fun updateSystemMessageInList(
        messages: List<DiaryChatMessage>,
        diaries: List<Diary>,
    ): List<DiaryChatMessage> {
        val mutableMessages = messages.toMutableList()

        // Remove existing system message
        mutableMessages.removeAll { it.role == DiaryChatRole.System }

        // Add a system message based on diary availability
        val systemMsg = if (diaries.isNotEmpty()) {
            try {
                DiaryChatMessage.System(
                    content = Json.encodeToString(diaries.map { it.toDto() }),
                )
            } catch (e: Exception) {
                logger.e(TAG) { "Error serializing diaries to JSON: $e" }
                systemMessage()
            }
        } else {
            systemMessage()
        }

        mutableMessages.add(0, systemMsg) // Add at the beginning
        return mutableMessages
    }

    fun queryDiaries(query: String) = viewModelScope.launch {
        if (query.isBlank()) {
            logger.w(TAG) { "Empty query provided" }
            return@launch
        }

        val userQuery = DiaryChatMessage.User(query)
        val currentMessages = viewState.value.messages
        val updatedMessages = currentMessages + userQuery

        _viewState.update {
            it.copy(
                isResponding = true,
                messages = updatedMessages,
                error = null,
            )
        }

        try {
            logger.d(TAG) { "queryDiaries: Querying all diaries for: $query" }

            val responseContent = diaryAI.queryDiaries(messages = updatedMessages)

            if (responseContent == null) {
                _viewState.update {
                    it.copy(
                        isResponding = false,
                        error = "Failed to generate AI response",
                    )
                }
                return@launch
            }

            val diaryAIResponse = DiaryChatMessage.DiaryAI(responseContent)
            val finalMessages = updatedMessages + diaryAIResponse

            _viewState.update {
                it.copy(
                    isResponding = false,
                    messages = finalMessages,
                    error = null,
                )
            }

            // Save messages asynchronously
            launch {
                try {
                    saveChatMessagePair(userQuery, diaryAIResponse)
                } catch (e: Exception) {
                    logger.e(TAG) { "Failed to save chat messages: $e" }
                }
            }
        } catch (e: Exception) {
            logger.e(TAG) { "Error in queryDiaries: $e" }
            _viewState.update {
                it.copy(
                    isResponding = false,
                    error = "An error occurred while processing your query",
                )
            }
        }
    }

    private fun saveChatMessagePair(
        userQuery: DiaryChatMessage,
        diaryAIResponse: DiaryChatMessage,
    ) =
        viewModelScope.launch {
            saveChatMessageUseCase(userQuery)
            saveChatMessageUseCase(diaryAIResponse)
            logger.i(TAG) { "Message pair saved to DB" }
        }

    private fun welcomeMessage() = DiaryChatMessage.DiaryAI(
        content = """
            Welcome to Diary AI.
            You can gain insights into your entries through interactive conversations.
        """.trimIndent(),
    )

    private fun systemMessage() = DiaryChatMessage.System(
        content = """
            You are Journal AI. I will provide you a json list of journal entries. Each entry contains the following fields: entry, date, and location the entry was made at.
            You will respond to follow-up questions based on this information. Do not respond to any questions outside the scope of the provided data and do not make a reference to the fact that you a list of json entries.
            Below is a sample structure of the json you are being fed:
            [
                {
                    "entry": "Some entry. \n,
                    "id": 835,
                    "date": "2025-05-28T22:54:52.157Z"
                }
            ]

            Here are a few things to note. An object in the array represents a single diary entry, you will be given a list of these, probably in the hundreds.
            You are supposed to analyse all the data in these objects. The entry ids are not relevant.
        """.trimIndent(),
    )

    fun clearError() {
        _viewState.update { it.copy(error = null) }
    }

    private companion object {
        private const val TAG = "DiaryChatViewModel"
    }
}
