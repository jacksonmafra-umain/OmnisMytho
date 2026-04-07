package com.umain.omnismytho

import androidx.compose.ui.window.ComposeUIViewController
import com.umain.omnismytho.data.local.AppDatabase
import com.umain.omnismytho.data.local.createAppDatabase
import com.umain.omnismytho.di.appModules
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun MainViewController() = ComposeUIViewController { App() }

fun initKoin() {
    startKoin {
        modules(
            module { single<AppDatabase> { createAppDatabase() } },
            *appModules.toTypedArray(),
        )
    }
}
