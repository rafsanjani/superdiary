package com.foreverrafs.superdiary.util

interface EntityMapper<Entity, Domain> {
    fun mapToDomain(entity: Entity): Domain
    fun mapToEntity(domainModel: Domain): Entity
    fun mapToEntityList(domainModel: List<Domain>): List<Entity>
    fun mapToDomainList(entityModel: List<Entity>): List<Domain>
}