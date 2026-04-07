package com.umain.omnismytho.domain.repository

import com.umain.omnismytho.domain.model.EntitySummary
import com.umain.omnismytho.domain.model.Mythology

interface MythologyRepository {
    suspend fun getMythologies(): List<Mythology>

    suspend fun getMythology(id: String): Mythology

    suspend fun getMythologyEntities(mythologyId: String): List<EntitySummary>
}
