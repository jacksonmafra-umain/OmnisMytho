package com.umain.omnismytho.data.remote

import com.umain.omnismytho.data.model.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class ApiService(
    private val client: HttpClient,
    private val baseUrl: String = ApiConfig.BASE_URL,
) {
    suspend fun getMythologies(): List<MythologyDto> =
        client.get("$baseUrl/mythologies").body()

    suspend fun getMythology(id: String): MythologyDetailDto =
        client.get("$baseUrl/mythologies/$id").body()

    suspend fun getEntities(
        mythologyId: String? = null,
        type: String? = null,
        alignment: String? = null,
        page: Int = 1,
        pageSize: Int = 20,
    ): PaginatedResponseDto =
        client
            .get("$baseUrl/entities") {
                mythologyId?.let { parameter("mythology_id", it) }
                type?.let { parameter("type", it) }
                alignment?.let { parameter("alignment", it) }
                parameter("page", page)
                parameter("page_size", pageSize)
            }.body()

    suspend fun searchEntities(
        query: String,
        limit: Int = 10,
    ): List<EntityDto> =
        client
            .get("$baseUrl/entities/search") {
                parameter("q", query)
                parameter("limit", limit)
            }.body()

    suspend fun getEntity(id: String): EntityDto =
        client.get("$baseUrl/entities/$id").body()
}
