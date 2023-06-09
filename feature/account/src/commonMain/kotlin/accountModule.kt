package com.github.nailkhaf.feature.account

import com.github.nailkhaf.data.account.accountModule
import com.github.nailkhaf.data.tokens.tokensModule
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val accountModule = module {
    includes(tokensModule, accountModule)
    singleOf(::addressValidator)
}