package com.foreverrafs.superdiary.ai.api

import com.foreverrafs.superdiary.ai.domain.model.DiaryChatMessage
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.WeeklySummary
import kotlinx.coroutines.flow.Flow

interface DiaryAI {
    /** Generates a diary entry of [wordCount] */
    fun generateDiary(prompt: String, wordCount: Int): Flow<String>

    /** Generates a summary from a list of diary entries. */
    fun generateSummary(
        diaries: List<Diary>,
        onCompletion: suspend (summary: WeeklySummary) -> Unit,
    ): Flow<String>

    suspend fun queryDiaries(
        messages: List<DiaryChatMessage>,
    ): String?

    companion object {
        const val QUERY_PROMPT = """
            You are Journal AI. I will provide you with a list of journal entries. Each entry includes the following fields:
            •	entry: the written content
            •	date: when it was recorded
            •	location: where it was written

        Your role is to answer follow-up questions strictly based on the information contained in these journal entries.

        Rules
            1.	Do not answer questions outside the scope of the provided data.
            2.	Never mention or reference the JSON format, the underlying array, or the fact that the entries were provided as structured data.
            3.	Each object represents a single diary entry. The list may contain hundreds of entries.
            4.	Analyse all entries when forming answers.
            5.	When responding:
                •	Use proper Markdown formatting so that the response can easily be rendered in any modern markdown reader.
                •	Keep responses concise, clear, and free of unnecessary detail unless essential.
            6.	DO NOT respond to any question outside the scope of the journal entries.
        """
    }
}
