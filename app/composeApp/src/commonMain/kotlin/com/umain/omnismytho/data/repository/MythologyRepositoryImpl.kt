package com.umain.omnismytho.data.repository

import com.umain.omnismytho.data.model.toDomain
import com.umain.omnismytho.data.remote.ApiService
import com.umain.omnismytho.domain.model.EntitySummary
import com.umain.omnismytho.domain.model.Mythology
import com.umain.omnismytho.domain.repository.MythologyRepository

class MythologyRepositoryImpl(
    private val apiService: ApiService,
) : MythologyRepository {

    override suspend fun getMythologies(): List<Mythology> =
        apiService.getMythologies().map { it.toDomain() }

    override suspend fun getMythology(id: String): Mythology =
        apiService.getMythology(id).toDomain()

    override suspend fun getMythologyEntities(mythologyId: String): List<EntitySummary> =
        apiService.getMythology(mythologyId).entities.map { it.toDomain() }
}
