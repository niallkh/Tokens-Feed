package com.github.nailkhaf.data.tokens.tokenlist

import com.github.nailkhaf.web3.models.Address
import kotlinx.coroutines.flow.Flow

fun interface TokenListProvider {
    operator fun invoke(tokenList: TokenList): Flow<List<Address>>
}