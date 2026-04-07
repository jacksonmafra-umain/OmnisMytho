package com.umain.omnismytho.di

import com.umain.omnismytho.data.local.AppDatabase
import org.koin.dsl.module

/**
 * Provides Room DAOs from the AppDatabase instance.
 * The AppDatabase itself must be provided by platform-specific modules
 * (androidDatabaseModule / iosDatabaseModule) since it needs platform context.
 */
val databaseModule = module {
    single { get<AppDatabase>().mythologyDao() }
    single { get<AppDatabase>().entityDao() }
    single { get<AppDatabase>().bookmarkDao() }
}
