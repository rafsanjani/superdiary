package com.foreverrafs.superdiary.util

interface EntityMapper<Entity, Domain> {
    fun mapToDomain(entity: Entity): Domain
    fun mapToEntity(domainModel: Domain): Entity
    fun mapToEntity(domainModel: List<Domain>): List<Entity>
    fun mapToDomain(entityModel: List<Entity>): List<Domain>
}