package com.github.nailkhaf.data.tokens

import com.github.nailkhaf.data.tokens.models.TokenBalance
import com.github.nailkhaf.data.tokens.tokenlist.TokenList
import com.github.nailkhaf.web3.models.Address
import kotlinx.coroutines.flow.Flow

interface ERC20TokensRepository {
    fun getTokenBalances(chainId: ULong = 1uL, account: Address): Flow<List<TokenBalance>>

    suspend fun detectNewERC20Tokens(
        chainId: ULong = 1uL,
        account: Address,
        tokenList: TokenList = TokenList.Rainbow,
    )
}
