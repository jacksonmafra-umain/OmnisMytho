package com.umain.omnismytho.di

import com.umain.omnismytho.data.repository.BookmarkRepositoryImpl
import com.umain.omnismytho.data.repository.EntityRepositoryImpl
import com.umain.omnismytho.data.repository.MythologyRepositoryImpl
import com.umain.omnismytho.domain.repository.BookmarkRepository
import com.umain.omnismytho.domain.repository.EntityRepository
import com.umain.omnismytho.domain.repository.MythologyRepository
import org.koin.dsl.module

val repositoryModule =
    module {
        single<MythologyRepository> { MythologyRepositoryImpl(get(), get()) }
        single<EntityRepository> { EntityRepositoryImpl(get(), get()) }
        single<BookmarkRepository> { BookmarkRepositoryImpl(get()) }
    }
