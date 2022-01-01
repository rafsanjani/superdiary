package com.foreverrafs.superdiary.framework.datasource.local.mapper

import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.framework.datasource.local.dto.DiaryDto
import com.foreverrafs.superdiary.util.EntityMapper
import java.time.LocalDateTime

class DiaryMapper : EntityMapper<DiaryDto, Diary> {
    override fun mapToDomain(entity: DiaryDto): Diary {
        return Diary(
            id = entity.id,
            message = entity.message,
            date = LocalDateTime.parse(entity.date),
            title = entity.title
        )
    }

    override fun mapToEntity(domainModel: Diary): DiaryDto {
        return DiaryDto(
            id = domainModel.id,
            message = domainModel.message,
            date = domainModel.date.toString(),
            title = domainModel.title
        )
    }

    override fun mapToEntity(domainModel: List<Diary>): List<DiaryDto> {
        return domainModel.map {
            mapToEntity(it)
        }
    }

    override fun mapToDomain(entityModel: List<DiaryDto>): List<Diary> {
        return entityModel.map {
            mapToDomain(it)
        }
    }
}