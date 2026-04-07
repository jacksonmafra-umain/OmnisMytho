package com.umain.omnismytho.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mythologies")
data class MythologyEntity(
    @PrimaryKey val id: String,
    val name: String,
    val origin: String,
    val description: String,
    val entityCount: Int,
    val lastUpdated: Long = System.currentTimeMillis(),
)

@Entity(tableName = "entities")
data class EntityRoom(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val title: String,
    val description: String,
    val appearance: String,
    val powers: String,       // JSON array serialized as string
    val symbols: String,      // JSON array serialized as string
    val personality: String,
    val alignment: String,
    val mythologyId: String,
    val imagePrompt: String,
    val lastUpdated: Long = System.currentTimeMillis(),
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val entityId: String,
    val createdAt: Long = System.currentTimeMillis(),
)
