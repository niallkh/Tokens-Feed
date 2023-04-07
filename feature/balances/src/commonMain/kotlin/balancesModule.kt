package com.github.nailkhaf.feature.balances

import com.github.nailkhaf.data.tokens.tokensModule
import org.koin.dsl.module

val balancesModule = module {
    includes(tokensModule)
}