package com.umain.omnismytho.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MythologyDao {
    @Query("SELECT * FROM mythologies ORDER BY name ASC")
    suspend fun getAll(): List<MythologyEntity>

    @Query("SELECT * FROM mythologies WHERE id = :id")
    suspend fun getById(id: String): MythologyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MythologyEntity>)

    @Query("SELECT COUNT(*) FROM mythologies")
    suspend fun count(): Int
}

@Dao
interface EntityDao {
    @Query("SELECT * FROM entities WHERE mythologyId = :mythologyId ORDER BY name ASC")
    suspend fun getByMythology(mythologyId: String): List<EntityRoom>

    @Query("SELECT * FROM entities WHERE id = :id")
    suspend fun getById(id: String): EntityRoom?

    @Query("SELECT * FROM entities WHERE name LIKE '%' || :query || '%' ORDER BY name ASC LIMIT :limit")
    suspend fun search(query: String, limit: Int): List<EntityRoom>

    @Query("SELECT * FROM entities ORDER BY name ASC LIMIT :pageSize OFFSET :offset")
    suspend fun getPage(pageSize: Int, offset: Int): List<EntityRoom>

    @Query("SELECT * FROM entities WHERE mythologyId = :mythologyId AND type = :type ORDER BY name ASC")
    suspend fun getByMythologyAndType(mythologyId: String, type: String): List<EntityRoom>

    @Query("SELECT COUNT(*) FROM entities")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM entities WHERE mythologyId = :mythologyId")
    suspend fun countByMythology(mythologyId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<EntityRoom>)

    @Query("SELECT * FROM entities WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<String>): List<EntityRoom>
}

@Dao
interface BookmarkDao {
    @Query("SELECT entityId FROM bookmarks ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<String>>

    @Query("SELECT entityId FROM bookmarks ORDER BY createdAt DESC")
    suspend fun getAll(): List<String>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE entityId = :entityId)")
    suspend fun isBookmarked(entityId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE entityId = :entityId")
    suspend fun delete(entityId: String)
}
