package com.foreverrafs.superdiary.insights

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.insights.domain.model.toWritingStats
import kotlin.test.Test
import kotlin.time.Instant

class WritingStatsTest {
    @Test
    fun `calculates writing statistics from entries`() {
        val stats = listOf(
            Diary(
                entry = "A short entry",
                date = Instant.fromEpochMilliseconds(0),
            ),
            Diary(
                entry = "A longer entry with several words",
                date = Instant.fromEpochMilliseconds(86_400_000),
            ),
        ).toWritingStats()

        assertThat(stats.entriesAnalyzed).isEqualTo(2)
        assertThat(stats.totalWords).isEqualTo(9)
        assertThat(stats.averageWordsPerEntry).isEqualTo(4)
        assertThat(stats.longestEntryWords).isEqualTo(6)
        assertThat(stats.activeDays).isEqualTo(2)
    }

    @Test
    fun `empty entries produce zero values`() {
        val stats = emptyList<Diary>().toWritingStats()

        assertThat(stats.entriesAnalyzed).isEqualTo(0)
        assertThat(stats.totalWords).isEqualTo(0)
        assertThat(stats.averageWordsPerEntry).isEqualTo(0)
        assertThat(stats.longestEntryWords).isEqualTo(0)
        assertThat(stats.activeDays).isEqualTo(0)
    }
}
