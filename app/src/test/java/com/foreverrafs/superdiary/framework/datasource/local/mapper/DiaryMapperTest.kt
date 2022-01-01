package com.foreverrafs.superdiary.framework.datasource.local.mapper

import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.framework.datasource.local.dto.DiaryDto
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDateTime

internal class DiaryMapperTest {
    private val entityMapper = DiaryMapper()

    @Test
    fun `map entity diary to domain confirm mapped`() {
        val entityDiary = DiaryDto(message = "Hello World", date = "2020-12-28T18:14:47.119", title = "")
        val domainDiary = entityMapper.mapToDomain(entityDiary)

        assertThat(entityDiary).isInstanceOf(DiaryDto::class.java)
        assertThat(domainDiary).isInstanceOf(Diary::class.java)

        assertThat(domainDiary.id).isEqualTo(entityDiary.id)
        assertThat(domainDiary.message).isEqualTo(entityDiary.message)
        assertThat(domainDiary.date).isEqualTo(LocalDateTime.parse(entityDiary.date))
    }

    @Test
    fun `map domain to diary entity confirm mapped`() {
        val domainDiary = Diary(message = "Hello World", title = "")
        val entityDiary = entityMapper.mapToEntity(domainDiary)

        assertThat(entityDiary).isInstanceOf(DiaryDto::class.java)
        assertThat(domainDiary).isInstanceOf(Diary::class.java)

        assertThat(entityDiary.id).isEqualTo(domainDiary.id)
        assertThat(entityDiary.message).isEqualTo(domainDiary.message)
        assertThat(entityDiary.date).isEqualTo(domainDiary.date.toString())
    }


}