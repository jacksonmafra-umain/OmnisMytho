package com.umain.omnismytho.domain.model

data class EntitySummary(
    val id: String,
    val name: String,
    val type: EntityType,
    val title: String,
    val alignment: Alignment,
    val mythologyId: String,
)
