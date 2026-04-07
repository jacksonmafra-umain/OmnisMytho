package com.umain.omnismytho.data.remote

import com.umain.omnismytho.data.model.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class ApiService(
    private val client: HttpClient,
) {
    companion object {
        // For Android emulator use 10.0.2.2, for iOS simulator use localhost
        const val BASE_URL = "http://10.0.2.2:8000/api/v1"
    }

    suspend fun getMythologies(): List<MythologyDto> = client.get("$BASE_URL/mythologies").body()

    suspend fun getMythology(id: String): MythologyDetailDto = client.get("$BASE_URL/mythologies/$id").body()

    suspend fun getEntities(
        mythologyId: String? = null,
        type: String? = null,
        alignment: String? = null,
        page: Int = 1,
        pageSize: Int = 20,
    ): PaginatedResponseDto =
        client
            .get("$BASE_URL/entities") {
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
            .get("$BASE_URL/entities/search") {
                parameter("q", query)
                parameter("limit", limit)
            }.body()

    suspend fun getEntity(id: String): EntityDto = client.get("$BASE_URL/entities/$id").body()
}
