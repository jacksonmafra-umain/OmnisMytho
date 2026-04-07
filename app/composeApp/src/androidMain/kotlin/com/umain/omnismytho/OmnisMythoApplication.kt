package com.umain.omnismytho

import android.app.Application
import com.umain.omnismytho.data.local.AppDatabase
import com.umain.omnismytho.data.local.createAppDatabase
import com.umain.omnismytho.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class OmnisMythoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@OmnisMythoApplication)
            modules(
                module { single<AppDatabase> { createAppDatabase(get()) } },
                *appModules.toTypedArray(),
            )
        }
    }
}
