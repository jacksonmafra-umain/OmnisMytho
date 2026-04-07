package com.umain.omnismytho.data.local

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

fun createAppDatabase(context: Context): AppDatabase =
    Room.databaseBuilder<AppDatabase>(
        context = context,
        name = context.getDatabasePath(AppDatabase.DB_NAME).absolutePath,
    )
        .setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()
