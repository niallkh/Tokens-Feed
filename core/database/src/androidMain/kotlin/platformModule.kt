package com.github.nailkhaf.database

import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import org.koin.dsl.module

internal actual val platformModule = module {
    single<SqlDriver> { AndroidSqliteDriver(Database.Schema, get(), "database.db") }
}