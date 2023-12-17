package com.foreverrafs.superdiary.diary.diaryai

import com.aallam.openai.api.chat.ChatMessage

fun DiaryChatMessage.toOpenAIChatMessage() = when (role) {
    DiaryChatRole.User -> ChatMessage.User(content)
    DiaryChatRole.DiaryAI -> ChatMessage.Assistant(content)
    DiaryChatRole.System -> ChatMessage.System(content)
}
