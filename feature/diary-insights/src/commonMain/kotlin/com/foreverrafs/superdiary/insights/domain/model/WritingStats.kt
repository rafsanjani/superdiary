package com.foreverrafs.superdiary.insights.domain.model

import com.foreverrafs.superdiary.domain.model.Diary

data class WritingStats(
    val entriesAnalyzed: Int,
    val totalWords: Int,
    val averageWordsPerEntry: Int,
    val longestEntryWords: Int,
)

fun List<Diary>.toWritingStats(): WritingStats {
    val wordCounts = map { diary -> diary.entry.wordCount() }

    return WritingStats(
        entriesAnalyzed = size,
        totalWords = wordCounts.sum(),
        averageWordsPerEntry = wordCounts.averageOrZero(),
        longestEntryWords = wordCounts.maxOrNull() ?: 0,
    )
}

private fun String.wordCount(): Int = trim()
    .split(WHITESPACE)
    .count { word -> word.isNotBlank() }

private fun List<Int>.averageOrZero(): Int =
    if (isEmpty()) 0 else (sum().toDouble() / size).toInt()

private val WHITESPACE = Regex("\\s+")
