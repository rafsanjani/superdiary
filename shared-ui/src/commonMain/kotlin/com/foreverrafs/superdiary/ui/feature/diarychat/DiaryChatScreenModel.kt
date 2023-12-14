package com.foreverrafs.superdiary.ui.feature.diarychat

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.foreverrafs.superdiary.diary.diaryai.DiaryAI
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DiaryChatScreenModel(
    private val diaryAI: DiaryAI,
    private val getAllDiariesUseCase: GetAllDiariesUseCase,
) :
    StateScreenModel<DiaryChatScreenModel.ChatScreenState>(ChatScreenState()) {
    data class ChatScreenState(
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

        getAllDiariesUseCase().collect {
            val response = diaryAI.queryDiaries(it, query)
            mutableState.update { state ->
                val messages = state.messages.append(
                    DiaryChatMessage.DiaryAI(
                        content = response,
                    ),
                )

                state.copy(
                    isResponding = false,
                    messages = messages,
                )
            }
        }
    }
}
