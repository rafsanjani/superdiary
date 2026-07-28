package com.foreverrafs.superdiary.insights.domain.model

enum class WritingInsightThemeType {
    Patterns,
    Consistency,
    TryNext,
}

data class WritingInsightTheme(
    val type: WritingInsightThemeType,
    val content: String,
) {
    val heading: String
        get() = when (type) {
            WritingInsightThemeType.Patterns -> "Your writing patterns"
            WritingInsightThemeType.Consistency -> "Your writing rhythm"
            WritingInsightThemeType.TryNext -> "Try this next"
        }
}

fun String.toWritingInsightThemes(): List<WritingInsightTheme> {
    val response = trim()
    if (response.isEmpty()) return emptyList()

    val matches = THEME_HEADING.findAll(response).toList()
    if (matches.isNotEmpty()) {
        return matches.mapIndexedNotNull { index, match ->
            val contentStart = match.range.last + 1
            val contentEnd = matches.getOrNull(index + 1)?.range?.first ?: response.length
            val content = response.substring(contentStart, contentEnd).trim()
            val type = match.groupValues[1].toThemeType() ?: return@mapIndexedNotNull null

            content.takeIf(String::isNotEmpty)?.let {
                WritingInsightTheme(type = type, content = it)
            }
        }
    }

    return response
        .split(BLANK_LINE)
        .filter(String::isNotBlank)
        .take(WritingInsightThemeType.entries.size)
        .mapIndexed { index, content ->
            WritingInsightTheme(
                type = WritingInsightThemeType.entries[index],
                content = content.trim(),
            )
        }
}

private fun String.toThemeType(): WritingInsightThemeType? = when (uppercase()) {
    "PATTERNS" -> WritingInsightThemeType.Patterns
    "CONSISTENCY" -> WritingInsightThemeType.Consistency
    "TRY NEXT" -> WritingInsightThemeType.TryNext
    else -> null
}

private val THEME_HEADING = Regex(
    pattern = """(?im)^\s*(?:\d+[.)]\s*)?(PATTERNS|CONSISTENCY|TRY NEXT)\s*[—–:-]\s*""",
)
private val BLANK_LINE = Regex("""\n\s*\n""")
