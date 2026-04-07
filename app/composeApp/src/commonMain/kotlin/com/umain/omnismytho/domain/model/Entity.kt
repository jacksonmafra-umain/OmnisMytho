package com.umain.omnismytho.domain.model

data class Entity(
    val id: String,
    val name: String,
    val type: EntityType,
    val title: String,
    val description: String,
    val appearance: String,
    val powers: List<String>,
    val symbols: List<String>,
    val personality: String,
    val alignment: Alignment,
    val mythologyId: String,
    val imagePrompt: String,
)

enum class EntityType(
    val displayName: String,
) {
    GOD("God"),
    DEMON("Demon"),
    ANGEL("Angel"),
    SPIRIT("Spirit"),
    CREATURE("Creature"),
    ;

    companion object {
        fun fromString(value: String): EntityType = entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: GOD
    }
}

enum class Alignment(
    val displayName: String,
) {
    GOOD("Good"),
    NEUTRAL("Neutral"),
    EVIL("Evil"),
    CHAOTIC("Chaotic"),
    ;

    companion object {
        fun fromString(value: String): Alignment = entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: NEUTRAL
    }
}
