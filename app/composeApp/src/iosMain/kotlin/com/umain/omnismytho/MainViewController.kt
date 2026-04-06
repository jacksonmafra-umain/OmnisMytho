package com.umain.omnismytho

import androidx.compose.ui.window.ComposeUIViewController
import com.umain.omnismytho.di.appModules
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController { App() }

fun initKoin() {
    startKoin {
        modules(appModules)
    }
}
