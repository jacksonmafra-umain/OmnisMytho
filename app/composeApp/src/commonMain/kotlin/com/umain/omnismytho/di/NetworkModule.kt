package com.umain.omnismytho.di

import com.umain.omnismytho.data.remote.ApiService
import com.umain.omnismytho.data.remote.createHttpClient
import org.koin.dsl.module

val networkModule = module {
    single { createHttpClient() }
    single { ApiService(get()) }
}
