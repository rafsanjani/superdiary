package com.foreverrafs.superdiary.diary.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.diary.diaryai.DiaryChatMessage
import com.foreverrafs.superdiary.diary.diaryai.toOpenAIChatMessage
import kotlin.test.Test

class MapperTest {
    @Test
    fun `Should map from domain user chat message to dto`() {
        val message = DiaryChatMessage.User("Hello World")
        val dto = message.toOpenAIChatMessage()

        assertThat(dto.content).isEqualTo(message.content)
    }

    @Test
    fun `Should map from domain system chat message to dto`() {
        val message = DiaryChatMessage.System("Hello World")
        val dto = message.toOpenAIChatMessage()

        assertThat(dto.content).isEqualTo(message.content)
    }

    @Test
    fun `Should map from domain AI chat message to dto`() {
        val message = DiaryChatMessage.DiaryAI("Hello World")
        val dto = message.toOpenAIChatMessage()

        assertThat(dto.content).isEqualTo(message.content)
    }
}
