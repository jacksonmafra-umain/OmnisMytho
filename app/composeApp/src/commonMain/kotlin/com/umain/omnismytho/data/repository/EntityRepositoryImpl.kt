package com.umain.omnismytho.data.repository

import com.umain.omnismytho.data.local.EntityDao
import com.umain.omnismytho.data.local.toDomain
import com.umain.omnismytho.data.local.toRoomEntity
import com.umain.omnismytho.data.model.toDomain
import com.umain.omnismytho.data.remote.ApiService
import com.umain.omnismytho.domain.model.*
import com.umain.omnismytho.domain.repository.EntityRepository
import kotlin.math.ceil

/**
 * Offline-first: fetch from API, cache to Room. If API fails, serve from cache.
 */
class EntityRepositoryImpl(
    private val apiService: ApiService,
    private val entityDao: EntityDao,
) : EntityRepository {

    override suspend fun getEntities(
        mythologyId: String?,
        type: EntityType?,
        alignment: Alignment?,
        page: Int,
        pageSize: Int,
    ): PaginatedResult<Entity> =
        try {
            val remote = apiService.getEntities(
                mythologyId = mythologyId,
                type = type?.name?.lowercase(),
                alignment = alignment?.name?.lowercase(),
                page = page,
                pageSize = pageSize,
            ).toDomain()
            // Cache fetched entities
            entityDao.insertAll(remote.items.map { it.toRoomEntity() })
            remote
        } catch (_: Exception) {
            // Fallback to cache
            val cached = if (mythologyId != null && type != null) {
                entityDao.getByMythologyAndType(mythologyId, type.name.lowercase())
            } else if (mythologyId != null) {
                entityDao.getByMythology(mythologyId)
            } else {
                entityDao.getPage(pageSize, (page - 1) * pageSize)
            }
            val items = cached.map { it.toDomain() }
            val total = if (mythologyId != null) {
                entityDao.countByMythology(mythologyId)
            } else {
                entityDao.count()
            }
            PaginatedResult(
                items = items,
                total = total,
                page = page,
                pageSize = pageSize,
                totalPages = maxOf(1, ceil(total.toDouble() / pageSize).toInt()),
            )
        }

    override suspend fun searchEntities(query: String, limit: Int): List<Entity> =
        try {
            val remote = apiService.searchEntities(query, limit).map { it.toDomain() }
            entityDao.insertAll(remote.map { it.toRoomEntity() })
            remote
        } catch (_: Exception) {
            entityDao.search(query, limit).map { it.toDomain() }
        }

    override suspend fun getEntity(id: String): Entity =
        try {
            val remote = apiService.getEntity(id).toDomain()
            entityDao.insertAll(listOf(remote.toRoomEntity()))
            remote
        } catch (_: Exception) {
            entityDao.getById(id)?.toDomain()
                ?: throw IllegalStateException("Entity $id not found in cache")
        }
}
