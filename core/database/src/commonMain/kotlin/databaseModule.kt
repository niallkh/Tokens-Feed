package com.github.nailkhaf.database

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val databaseModule = module {
    includes(platformModule)
    single { Erc20Token.Adapter(ByteStringAdapter) }
    single {
        Erc20Transfer.Adapter(
            ByteStringAdapter,
            ByteStringAdapter,
            ByteStringAdapter,
            BigIntegerAdapter
        )
    }
    singleOf(Database::invoke)
    single { get<Database>().erc20TokenQueries }
    single { get<Database>().erc20TransferQueries }
}

internal expect val platformModule: Module
