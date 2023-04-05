package com.github.nailkhaf.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Storage

class AppStore(
    private val storage: Storage<Store>
) : DataStore<Store> by DataStoreFactory.create(storage)
