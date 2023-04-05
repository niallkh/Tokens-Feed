package com.github.nailkhaf.datastore

import androidx.datastore.core.okio.OkioSerializer
import okio.BufferedSink
import okio.BufferedSource

object StoreSerializer : OkioSerializer<Store> {
    override val defaultValue: Store = Store()

    override suspend fun readFrom(source: BufferedSource): Store {
        return Store.ADAPTER.decode(source)
    }

    override suspend fun writeTo(t: Store, sink: BufferedSink) {
        Store.ADAPTER.encode(sink, t)
    }
}

