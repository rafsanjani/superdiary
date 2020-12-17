package com.foreverrafs.superdiary.framework.datasource.local.mapper

import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.framework.datasource.local.model.DiaryEntity
import com.foreverrafs.superdiary.util.EntityMapper
import java.time.LocalDateTime

class DiaryMapper : EntityMapper<DiaryEntity, Diary> {
    override fun mapToDomain(entity: DiaryEntity): Diary {
        return Diary(
            id = entity.id,
            message = entity.title,
            date = LocalDateTime.parse(entity.date)
        )
    }

    override fun mapToEntity(domainModel: Diary): DiaryEntity {
        return DiaryEntity(
            id = domainModel.id,
            title = domainModel.message,
            date = domainModel.date.toString()
        )
    }

    override fun mapToEntityList(domainModel: List<Diary>): List<DiaryEntity> {
        return domainModel.map {
            mapToEntity(it)
        }
    }

    override fun mapToDomainList(entityModel: List<DiaryEntity>): List<Diary> {
        return entityModel.map {
            mapToDomain(it)
        }
    }
}