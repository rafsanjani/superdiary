package com.foreverrafs.superdiary.ui.feature.diarychat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.diaryai.DiaryAI
import com.foreverrafs.superdiary.data.diaryai.DiaryChatMessage
import com.foreverrafs.superdiary.data.diaryai.DiaryChatRole
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.data.usecase.GetChatMessagesUseCase
import com.foreverrafs.superdiary.data.usecase.SaveChatMessageUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

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
            logger.d(TAG) { "New subscription to DiaryChatViewModel" }
            loadDiaries()
        }
        .onCompletion {
            logger.d(TAG) { "All subscriptions to DiaryChatViewModel removed" }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DiaryChatViewState())

    private val diariesFlow: Flow<List<Diary>> = getAllDiariesUseCase()
    private val chatMessagesFlow: Flow<List<DiaryChatMessage>> = getChatMessagesUseCase()

    private fun loadDiaries() = viewModelScope.launch {
        _viewState.updateLoadingDiaries(true)
        diariesFlow.collect { diaries ->
            _viewState.update { it.copy(diaries = diaries, isLoadingDiaries = false) }
            logger.i(TAG) { "Loaded all diaries for chat screen: Size = ${diaries.size}" }
            updateChatMessageList(diaries)
        }
    }

    private fun updateChatMessageList(diaries: List<Diary>) = viewModelScope.launch {
        chatMessagesFlow.collect { messages ->
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
            mutableMessages += DiaryChatMessage.System(diaries.joinToString())
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
            You are Journal AI. I will provide you a list of journal entries and their dates. You will respond to follow-up questions based on this information. Do not respond to any questions outside the scope of the provided data.
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
            update { it.copy(isResponding = isResponding, messages = messages) }
        }
    }
}
