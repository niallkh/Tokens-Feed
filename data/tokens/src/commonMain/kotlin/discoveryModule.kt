package com.github.nailkhaf.data.tokens

import com.github.nailkhaf.database.databaseModule
import com.github.nailkhaf.datastore.datastoreModule
import com.github.nailkhaf.web3.web3Module
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val tokensModule = module {
    includes(platformModule, web3Module, databaseModule, datastoreModule)
    singleOf(::DefaultERC20TransfersRepository).bind<ERC20TransfersRepository>()
    singleOf(::DefaultERC20TokensRepository).bind<ERC20TokensRepository>()
}

internal expect val platformModule: Module