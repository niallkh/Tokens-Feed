package com.github.nailkhaf.feature.transfers

import com.github.nailkhaf.data.account.accountModule
import com.github.nailkhaf.data.tokens.tokensModule
import org.koin.dsl.module

val transfersModule = module {
    includes(tokensModule, accountModule)
}
