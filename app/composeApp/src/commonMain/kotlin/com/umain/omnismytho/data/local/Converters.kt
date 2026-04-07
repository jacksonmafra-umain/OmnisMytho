package com.umain.omnismytho.data.local

import com.umain.omnismytho.domain.model.*

fun MythologyEntity.toDomain() = Mythology(
    id = id,
    name = name,
    origin = origin,
    description = description,
    entityCount = entityCount,
)

fun Mythology.toEntity() = MythologyEntity(
    id = id,
    name = name,
    origin = origin,
    description = description,
    entityCount = entityCount,
)

fun EntityRoom.toDomain() = com.umain.omnismytho.domain.model.Entity(
    id = id,
    name = name,
    type = EntityType.fromString(type),
    title = title,
    description = description,
    appearance = appearance,
    powers = deserializeList(powers),
    symbols = deserializeList(symbols),
    personality = personality,
    alignment = Alignment.fromString(alignment),
    mythologyId = mythologyId,
    imagePrompt = imagePrompt,
)

fun com.umain.omnismytho.domain.model.Entity.toRoomEntity() = EntityRoom(
    id = id,
    name = name,
    type = type.name.lowercase(),
    title = title,
    description = description,
    appearance = appearance,
    powers = serializeList(powers),
    symbols = serializeList(symbols),
    personality = personality,
    alignment = alignment.name.lowercase(),
    mythologyId = mythologyId,
    imagePrompt = imagePrompt,
)

// Simple JSON-like serialization for list of strings (no extra deps needed)
private fun serializeList(list: List<String>): String =
    list.joinToString("||")

private fun deserializeList(raw: String): List<String> =
    if (raw.isBlank()) emptyList() else raw.split("||")
