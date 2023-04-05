package com.github.nailkhaf.datastore

import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal const val DATASTORE_STORE = "com.github.nailkhaf.datastore.store"

val datastoreModule = module {
    includes(platformModule)
    single { AppStore(get(named(DATASTORE_STORE))) }
}

internal expect val platformModule: Module
