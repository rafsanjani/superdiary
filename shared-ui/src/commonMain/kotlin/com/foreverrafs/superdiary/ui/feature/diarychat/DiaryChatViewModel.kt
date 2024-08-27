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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    private val mutableState = MutableStateFlow(DiaryChatViewState())
    val state: StateFlow<DiaryChatViewState> = mutableState.asStateFlow()

    private val chatMessages = getChatMessagesUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList(),
    )

    private val localDiaries = getAllDiariesUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList(),
    )

    fun init() {
        loadDiaries()
    }

    private fun updateChatMessageList(diaries: List<Diary>) = viewModelScope.launch {
        logger.i(TAG) {
            "Loading chat messages from DB"
        }

        chatMessages.collect {
            val messages = it.toMutableList()

            logger.i(TAG) {
                "Chat messages have been loaded from DB: Size = ${messages.size}"
            }

            val welcomeMessage = DiaryChatMessage.DiaryAI(
                content = """
                    Welcome to Diary AI.
                    You can gain insights into your entries through interactive conversations.
                """.trimIndent(),
            )

            if (messages.isEmpty()) {
                messages.add(welcomeMessage)
            }

            mutableState.update { state ->
                state.copy(messages = messages)
            }

            // We use the previous chat messages, in addition to the diaries to provide the working context
            updateSystemMessage(messages = messages, diaries = diaries)
        }
    }

    private fun updateSystemMessage(
        messages: List<DiaryChatMessage>,
        diaries: List<Diary>,
    ) {
        // convert the messages to mutable list
        val mutableListMessages = messages.toMutableList()

        // Only add the system prompt if we are querying for the first time
        if (mutableListMessages.none { it.role == DiaryChatRole.System }) {
            val systemMessage = DiaryChatMessage.System(
                """
                            You are Journal AI, I will provide you a list of journal entries and their dates and you will
                            respond to follow up questions based on this information. You are not supposed to respond to
                            any questions outside of the scope of the data you have been given under any circumstances.
                """.trimIndent(),
            )
            mutableListMessages.add(
                systemMessage,
            )
        }

        // Add the diaries
        mutableListMessages.add(DiaryChatMessage.System(diaries.joinToString()))

        mutableState.update {
            it.copy(
                messages = mutableListMessages,
            )
        }
    }

    private fun loadDiaries() = viewModelScope.launch {
        logger.i(TAG) {
            "Loading diaries for chat screen"
        }

        mutableState.update { state ->
            state.copy(
                isLoadingDiaries = true,
            )
        }

        localDiaries.collect { diaries ->
            mutableState.update { state ->
                logger.i(TAG) {
                    "Loaded all diaries for chat screen: Size = ${diaries.size}"
                }

                state.copy(
                    diaries = diaries,
                    isLoadingDiaries = false,
                )
            }

            // We want to only update the chat message list after the local diaries have been loaded
            // to have an up to date diary entries
            updateChatMessageList(diaries = diaries)
        }
    }

    fun queryDiaries(query: String) = viewModelScope.launch {
        logger.d(TAG) {
            "queryDiaries: Querying all diaries for: $query"
        }

        // Let's grab all the messages in the system
        val messages = mutableState.value.messages.toMutableList()

        // Update state to reflect responding state in the UI
        val userQuery = DiaryChatMessage.User(query)

        mutableState.update { state ->
            state.copy(
                isResponding = true,
                messages = state.messages.append(
                    userQuery,
                ),
            )
        }

        // Add the user query
        messages.add(userQuery)

        val response = diaryAI.queryDiaries(
            messages = messages,
        )

        mutableState.update { state ->
            logger.d(TAG) {
                "queryDiaries: Finished responding to AI query"
            }

            val diaryAiResponse = DiaryChatMessage.DiaryAI(content = response)
            messages.add(diaryAiResponse)

            saveChatMessagePair(
                userQuery = userQuery,
                diaryAiResponse = diaryAiResponse,
            )

            state.copy(
                isResponding = false,
                messages = messages,
            )
        }
    }

    private fun saveChatMessagePair(
        userQuery: DiaryChatMessage,
        diaryAiResponse: DiaryChatMessage,
    ) =
        viewModelScope.launch {
            saveChatMessageUseCase(userQuery)
            saveChatMessageUseCase(diaryAiResponse)

            logger.i(TAG) {
                "Message pair saved to DB"
            }
        }

    private fun <T> List<T>.append(item: T): List<T> =
        toMutableList().also { it.add(item) }.toList()

    companion object {
        private const val TAG = "DiaryChatViewModel"
    }
}
