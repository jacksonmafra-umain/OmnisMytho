package com.umain.omnismytho.data.repository

import com.umain.omnismytho.data.model.toDomain
import com.umain.omnismytho.data.remote.ApiService
import com.umain.omnismytho.domain.model.*
import com.umain.omnismytho.domain.repository.EntityRepository

class EntityRepositoryImpl(
    private val apiService: ApiService,
) : EntityRepository {

    override suspend fun getEntities(
        mythologyId: String?,
        type: EntityType?,
        alignment: Alignment?,
        page: Int,
        pageSize: Int,
    ): PaginatedResult<Entity> =
        apiService.getEntities(
            mythologyId = mythologyId,
            type = type?.name?.lowercase(),
            alignment = alignment?.name?.lowercase(),
            page = page,
            pageSize = pageSize,
        ).toDomain()

    override suspend fun searchEntities(query: String, limit: Int): List<Entity> =
        apiService.searchEntities(query, limit).map { it.toDomain() }

    override suspend fun getEntity(id: String): Entity =
        apiService.getEntity(id).toDomain()
}
