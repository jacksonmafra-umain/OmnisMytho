package com.umain.omnismytho.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MythologyDto(
    val id: String,
    val name: String,
    val origin: String,
    val description: String,
    @SerialName("entity_count") val entityCount: Int,
)

@Serializable
data class MythologyDetailDto(
    val id: String,
    val name: String,
    val origin: String,
    val description: String,
    @SerialName("entity_count") val entityCount: Int,
    val entities: List<EntitySummaryDto>,
)

@Serializable
data class EntityDto(
    val id: String,
    val name: String,
    val type: String,
    val title: String,
    val description: String,
    val appearance: String,
    val powers: List<String>,
    val symbols: List<String>,
    val personality: String,
    val alignment: String,
    @SerialName("mythology_id") val mythologyId: String,
    @SerialName("image_prompt") val imagePrompt: String,
)

@Serializable
data class EntitySummaryDto(
    val id: String,
    val name: String,
    val type: String,
    val title: String,
    val alignment: String,
    @SerialName("mythology_id") val mythologyId: String,
)

@Serializable
data class PaginatedResponseDto(
    val items: List<EntityDto>,
    val total: Int,
    val page: Int,
    @SerialName("page_size") val pageSize: Int,
    @SerialName("total_pages") val totalPages: Int,
)
