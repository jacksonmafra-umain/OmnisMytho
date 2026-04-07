package com.umain.omnismytho.domain.repository

import com.umain.omnismytho.domain.model.*

interface EntityRepository {
    suspend fun getEntities(
        mythologyId: String? = null,
        type: EntityType? = null,
        alignment: Alignment? = null,
        page: Int = 1,
        pageSize: Int = 20,
    ): PaginatedResult<Entity>

    suspend fun searchEntities(
        query: String,
        limit: Int = 10,
    ): List<Entity>

    suspend fun getEntity(id: String): Entity
}
