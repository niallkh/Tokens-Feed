package com.github.nailkhaf.data.tokens

import com.github.nailkhaf.data.tokens.tokenlist.AndroidTokenListProvider
import com.github.nailkhaf.data.tokens.tokenlist.TokenListProvider
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val platformModule = module {
    singleOf(::AndroidTokenListProvider).bind<TokenListProvider>()
}