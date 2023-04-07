package com.github.nailkhaf.data.account

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val accountModule = module {
    singleOf(::DefaultAccountRepository).bind<AccountRepository>()
}