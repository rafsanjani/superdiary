package com.foreverrafs.superdiary.ui.feature.diarychat

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.foreverrafs.superdiary.diary.diaryai.DiaryAI
import com.foreverrafs.superdiary.diary.diaryai.DiaryChatMessage
import com.foreverrafs.superdiary.diary.diaryai.DiaryChatRole
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DiaryChatViewModel(
    private val diaryAI: DiaryAI,
    private val getAllDiariesUseCase: GetAllDiariesUseCase,
) : StateScreenModel<DiaryChatViewModel.DiaryChatViewState>(DiaryChatViewState()) {
    data class DiaryChatViewState(
        val isResponding: Boolean = false,
        val messages: List<DiaryChatMessage> = listOf(
            DiaryChatMessage.DiaryAI(
                content = """
                    Welcome to Diary AI.
                    You can gain insights into your entries through interactive conversations.
                """.trimIndent(),
            ),
        ),
    )

    private fun <T> List<T>.append(item: T): List<T> {
        return this.toMutableList().also { it.add(item) }.toList()
    }

    fun queryDiaries(query: String) = screenModelScope.launch {
        // Let's grab all the messages in the system
        val diaryChatList = mutableState.value.messages.toMutableList()

        // Update state to reflect responding state in the UI
        mutableState.update { state ->
            state.copy(
                isResponding = true,
                messages = state.messages.append(
                    DiaryChatMessage.User(
                        content = query,
                    ),
                ),
            )
        }

        getAllDiariesUseCase().collect { diaries ->
            // Only add the system prompt if we are querying for the first time
            if (diaryChatList.none { it.role == DiaryChatRole.User }) {
                diaryChatList.add(
                    DiaryChatMessage.System(
                        """
                            You are Journal AI, I will provide you a list of journal entries and their dates and you will 
                            respond to follow up questions based on this information. You are not supposed to respond to 
                            any questions outside of the scope of the data you have been given under any circumstances.
                            Your responses should be very concise and if you don't have the answer to a question, simply let
                            the user know that you are only able to assist with information contained in their entries.
                        """.trimIndent(),
                    ),
                )
            }

            // Add the diaries
            diaryChatList.add(DiaryChatMessage.System(diaries.joinToString()))

            // Add the user query
            diaryChatList.add(DiaryChatMessage.User(query))

            val response = diaryAI.queryDiaries(
                messages = diaryChatList,
            )

            mutableState.update { state ->
                diaryChatList.add(DiaryChatMessage.DiaryAI(content = response))

                state.copy(
                    isResponding = false,
                    messages = diaryChatList,
                )
            }
        }
    }
}
