package com.foreverrafs.superdiary.insights

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.insights.domain.model.WritingInsightThemeType
import com.foreverrafs.superdiary.insights.domain.model.toWritingInsightThemes
import kotlin.test.Test

class WritingInsightThemeTest {
    @Test
    fun `parses each named insight into its own theme`() {
        val themes = """
            PATTERNS: Entries are becoming more descriptive.
            CONSISTENCY: Writing happens most often at weekends.
            TRY NEXT: Capture one small detail each day.
        """.trimIndent().toWritingInsightThemes()

        assertThat(themes.map { it.type }).containsExactly(
            WritingInsightThemeType.Patterns,
            WritingInsightThemeType.Consistency,
            WritingInsightThemeType.TryNext,
        )
        assertThat(themes[0].content).isEqualTo("Entries are becoming more descriptive.")
        assertThat(themes[1].content).isEqualTo("Writing happens most often at weekends.")
        assertThat(themes[2].content).isEqualTo("Capture one small detail each day.")
    }

    @Test
    fun `supports paragraph responses when headings are missing`() {
        val themes = """
            First observation.

            Second observation.

            Third observation.
        """.trimIndent().toWritingInsightThemes()

        assertThat(themes.size).isEqualTo(3)
        assertThat(themes.last().type).isEqualTo(WritingInsightThemeType.TryNext)
    }
}
