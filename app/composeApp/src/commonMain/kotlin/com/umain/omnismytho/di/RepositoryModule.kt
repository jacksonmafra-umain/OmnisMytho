package com.umain.omnismytho.di

import com.umain.omnismytho.data.repository.EntityRepositoryImpl
import com.umain.omnismytho.data.repository.MythologyRepositoryImpl
import com.umain.omnismytho.domain.repository.EntityRepository
import com.umain.omnismytho.domain.repository.MythologyRepository
import org.koin.dsl.module

val repositoryModule =
    module {
        single<MythologyRepository> { MythologyRepositoryImpl(get()) }
        single<EntityRepository> { EntityRepositoryImpl(get()) }
    }
