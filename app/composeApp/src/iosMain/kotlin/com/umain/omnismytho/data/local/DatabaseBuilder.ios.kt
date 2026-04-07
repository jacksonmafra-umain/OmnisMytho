package com.umain.omnismytho.data.local

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import platform.Foundation.NSHomeDirectory

fun createAppDatabase(): AppDatabase =
    Room.databaseBuilder<AppDatabase>(
        name = NSHomeDirectory() + "/Documents/${AppDatabase.DB_NAME}",
    )
        .setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()
