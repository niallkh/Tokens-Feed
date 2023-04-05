package com.github.nailkhaf.data.tokens

import com.github.nailkhaf.data.tokens.models.Erc20TokenBalance
import com.github.nailkhaf.data.tokens.tokenlist.TokenList
import com.github.nailkhaf.web3.models.Address
import kotlinx.coroutines.flow.Flow

interface ERC20TokensRepository {
    fun getTokenBalances(chainId: ULong, account: Address): Flow<List<Erc20TokenBalance>>

    suspend fun detectNewERC20Tokens(
        chainId: ULong,
        account: Address,
        tokenList: TokenList = TokenList.Rainbow,
    )
}
