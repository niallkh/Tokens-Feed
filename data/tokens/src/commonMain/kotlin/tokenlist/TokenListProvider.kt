package com.github.nailkhaf.data.tokens.tokenlist

import com.github.nailkhaf.web3.models.Address

fun interface TokenListProvider {
    suspend operator fun invoke(tokenList: TokenList): List<Address>
}