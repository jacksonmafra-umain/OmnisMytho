package com.umain.omnismytho.di

import com.umain.omnismytho.presentation.viewmodel.*
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModel { HomeViewModel(get()) }
        viewModel { params -> CatalogViewModel(params.get(), get()) }
        viewModel { params -> DetailViewModel(params.get(), get()) }
        viewModel { SearchViewModel(get()) }
    }
