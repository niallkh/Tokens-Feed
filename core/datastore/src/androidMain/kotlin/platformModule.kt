package com.github.nailkhaf.datastore

import android.content.Context
import androidx.datastore.core.Storage
import androidx.datastore.core.okio.OkioStorage
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal actual val platformModule = module {
    single<Storage<*>>(named(DATASTORE_STORE)) {
        OkioStorage(FileSystem.SYSTEM, StoreSerializer) {
            get<Context>().filesDir.resolve("store.dp").toOkioPath()
        }
    }
}
