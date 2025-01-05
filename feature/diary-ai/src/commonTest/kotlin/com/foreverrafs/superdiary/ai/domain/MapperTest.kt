package com.foreverrafs.superdiary.ai.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.ai.domain.model.DiaryChatMessage
import com.foreverrafs.superdiary.ai.domain.model.toNetworkChatMessage
import kotlin.test.Test

class MapperTest {
    @Test
    fun `Should map from domain user chat message to dto`() {
        val message = DiaryChatMessage.User("Hello World")
        val dto = message.toNetworkChatMessage()

        assertThat(dto.content).isEqualTo(message.content)
    }

    @Test
    fun `Should map from domain system chat message to dto`() {
        val message = DiaryChatMessage.System("Hello World")
        val dto = message.toNetworkChatMessage()

        assertThat(dto.content).isEqualTo(message.content)
    }

    @Test
    fun `Should map from domain AI chat message to dto`() {
        val message = DiaryChatMessage.DiaryAI("Hello World")
        val dto = message.toNetworkChatMessage()

        assertThat(dto.content).isEqualTo(message.content)
    }
}
