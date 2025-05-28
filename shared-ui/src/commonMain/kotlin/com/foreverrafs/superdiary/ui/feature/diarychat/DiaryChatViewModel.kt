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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
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
    )

    private val _viewState = MutableStateFlow(DiaryChatViewState())
    val viewState: StateFlow<DiaryChatViewState> = _viewState
        .onStart {
            loadDiaries()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DiaryChatViewState(),
        )

    private val diaries: Flow<Result<List<Diary>>> = getAllDiariesUseCase()
    private val chatMessages: Flow<List<DiaryChatMessage>> = getChatMessagesUseCase()

    private fun loadDiaries() = viewModelScope.launch {
        _viewState.updateLoadingDiaries(true)
        diaries.collect { result ->
            when (result) {
                is Result.Success -> {
                    _viewState.update {
                        it.copy(
                            diaries = result.data,
                            isLoadingDiaries = false,
                        )
                    }
                    logger.i(TAG) { "Loaded all result for chat screen: Size = ${result.data.size}" }
                    updateChatMessageList(result.data)
                }

                is Result.Failure -> {
                    logger.e(TAG) {
                        "Error loading diaries"
                    }
                }
            }
        }
    }

    private fun updateChatMessageList(diaries: List<Diary>) = viewModelScope.launch {
        chatMessages.collect { messages ->
            val updatedMessages = messages.toMutableList()
            if (updatedMessages.isEmpty()) updatedMessages += welcomeMessage()
            _viewState.update { it.copy(messages = updatedMessages) }
            updateSystemMessage(updatedMessages, diaries)
        }
    }

    private fun updateSystemMessage(messages: List<DiaryChatMessage>, diaries: List<Diary>) {
        val mutableMessages = messages.toMutableList()
        if (mutableMessages.none { it.role == DiaryChatRole.System }) {
            mutableMessages += systemMessage()
        }
        if (diaries.isNotEmpty()) {
            // delete the system message if it's there
            mutableMessages.firstOrNull { it.role == DiaryChatRole.System }?.let {
                mutableMessages.remove(it)
            }

            mutableMessages += DiaryChatMessage.System(
                content = Json.encodeToString(diaries.map { it.toDto() }),
            )
        }
        _viewState.update { it.copy(messages = mutableMessages) }
    }

    fun queryDiaries(query: String) = viewModelScope.launch {
        val userQuery = DiaryChatMessage.User(query)
        val updatedMessages = viewState.value.messages + userQuery

        _viewState.updateRespondingState(
            isResponding = true,
            messages = updatedMessages,
        )

        yield()
        logger.d(TAG) { "queryDiaries: Querying all diaries for: $query" }

        val responseContent = diaryAI.queryDiaries(messages = updatedMessages)

        // There was an error generating the response
        if (responseContent == null) {
            _viewState.updateRespondingState(
                isResponding = false,
            )
            return@launch
        }

        val diaryAIResponse = DiaryChatMessage.DiaryAI(responseContent)

        val finalMessages = updatedMessages + diaryAIResponse
        saveChatMessagePair(userQuery, diaryAIResponse)

        _viewState.updateRespondingState(
            isResponding = false,
            messages = finalMessages,
        )
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

    private companion object {
        private const val TAG = "DiaryChatViewModel"

        private fun MutableStateFlow<DiaryChatViewState>.updateLoadingDiaries(isLoading: Boolean) {
            update { it.copy(isLoadingDiaries = isLoading) }
        }

        private fun MutableStateFlow<DiaryChatViewState>.updateRespondingState(
            isResponding: Boolean,
            messages: List<DiaryChatMessage>,
        ) {
            update { it.copy(isResponding = isResponding, messages = it.messages + messages) }
        }

        private fun MutableStateFlow<DiaryChatViewState>.updateRespondingState(
            isResponding: Boolean,
        ) {
            update { it.copy(isResponding = isResponding) }
        }
    }
}
