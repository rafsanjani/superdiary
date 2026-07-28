package com.foreverrafs.superdiary.insights

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.insights.data.WritingInsightsRepositoryImpl
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlin.test.Test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class WritingInsightsRepositoryTest {
    private val dataSource = mock<DataSource>()
    private val diaryAI = mock<DiaryAI>()

    @Test
    fun `observes the complete local diary collection`() = runTest {
        val allEntries = List(125) { index -> Diary(entry = "Entry $index") }
        every { dataSource.getLatest(Int.MAX_VALUE) } returns flowOf(allEntries)

        val repository = WritingInsightsRepositoryImpl(dataSource, diaryAI)

        assertThat(repository.observeEntries().first().size).isEqualTo(125)
    }
}
