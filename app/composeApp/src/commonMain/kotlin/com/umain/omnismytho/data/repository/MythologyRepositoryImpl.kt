package com.umain.omnismytho.data.repository

import com.umain.omnismytho.data.local.MythologyDao
import com.umain.omnismytho.data.local.toDomain
import com.umain.omnismytho.data.local.toEntity
import com.umain.omnismytho.data.model.toDomain
import com.umain.omnismytho.data.remote.ApiService
import com.umain.omnismytho.domain.model.EntitySummary
import com.umain.omnismytho.domain.model.Mythology
import com.umain.omnismytho.domain.repository.MythologyRepository

/**
 * Offline-first: fetch from API, cache to Room. If API fails, serve from cache.
 */
class MythologyRepositoryImpl(
    private val apiService: ApiService,
    private val mythologyDao: MythologyDao,
) : MythologyRepository {

    override suspend fun getMythologies(): List<Mythology> =
        try {
            val remote = apiService.getMythologies().map { it.toDomain() }
            mythologyDao.insertAll(remote.map { it.toEntity() })
            remote
        } catch (_: Exception) {
            mythologyDao.getAll().map { it.toDomain() }
        }

    override suspend fun getMythology(id: String): Mythology =
        try {
            val remote = apiService.getMythology(id).toDomain()
            mythologyDao.insertAll(listOf(remote.toEntity()))
            remote
        } catch (_: Exception) {
            mythologyDao.getById(id)?.toDomain()
                ?: throw IllegalStateException("Mythology $id not found in cache")
        }

    override suspend fun getMythologyEntities(mythologyId: String): List<EntitySummary> =
        apiService.getMythology(mythologyId).entities.map { it.toDomain() }
}
