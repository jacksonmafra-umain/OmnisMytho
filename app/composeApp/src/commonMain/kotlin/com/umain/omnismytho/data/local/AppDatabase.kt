package com.umain.omnismytho.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.ConstructedBy
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [MythologyEntity::class, EntityRoom::class, BookmarkEntity::class],
    version = 1,
    exportSchema = true,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mythologyDao(): MythologyDao
    abstract fun entityDao(): EntityDao
    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        const val DB_NAME = "omnismytho.db"
    }
}

// Room KMP requires this — the compiler generates the actual implementation
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
