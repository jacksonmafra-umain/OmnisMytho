package com.umain.omnismytho.data.model

import com.umain.omnismytho.domain.model.*

fun MythologyDto.toDomain() = Mythology(
    id = id,
    name = name,
    origin = origin,
    description = description,
    entityCount = entityCount,
)

fun MythologyDetailDto.toDomain() = Mythology(
    id = id,
    name = name,
    origin = origin,
    description = description,
    entityCount = entityCount,
)

fun EntityDto.toDomain() = Entity(
    id = id,
    name = name,
    type = EntityType.fromString(type),
    title = title,
    description = description,
    appearance = appearance,
    powers = powers,
    symbols = symbols,
    personality = personality,
    alignment = Alignment.fromString(alignment),
    mythologyId = mythologyId,
    imagePrompt = imagePrompt,
)

fun EntitySummaryDto.toDomain() = EntitySummary(
    id = id,
    name = name,
    type = EntityType.fromString(type),
    title = title,
    alignment = Alignment.fromString(alignment),
    mythologyId = mythologyId,
)

fun PaginatedResponseDto.toDomain() = PaginatedResult(
    items = items.map { it.toDomain() },
    total = total,
    page = page,
    pageSize = pageSize,
    totalPages = totalPages,
)
