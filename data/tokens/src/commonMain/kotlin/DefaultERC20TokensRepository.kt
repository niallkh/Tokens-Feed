package com.github.nailkhaf.data.tokens

import com.github.nailkhaf.data.tokens.models.*
import com.github.nailkhaf.data.tokens.tokenlist.TokenList
import com.github.nailkhaf.data.tokens.tokenlist.TokenListProvider
import com.github.nailkhaf.database.Erc20TokenQueries
import com.github.nailkhaf.web3.Web3Provider
import com.github.nailkhaf.web3.abi.contractCall
import com.github.nailkhaf.web3.contracts.ERC20
import com.github.nailkhaf.web3.contracts.Multicall3
import com.github.nailkhaf.web3.models.Address
import com.github.nailkhaf.web3.models.asAddress
import com.github.nailkhaf.web3.multiCall
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import com.github.nailkhaf.database.Erc20Token as Erc20TokenDB

class DefaultERC20TokensRepository(
    private val web3: Web3Provider,
    private val tokenQueries: Erc20TokenQueries,
    private val tokenListProvider: TokenListProvider
) : ERC20TokensRepository {

    override fun getTokenBalances(
        chainId: ULong,
        account: Address
    ): Flow<List<TokenBalance>> = tokenQueries.selectAll(listOf(chainId.toLong()))
        .asFlow()
        .mapToList()
        .map { getTokenBalances(chainId, account, it) }

    private suspend fun getTokenBalances(
        chainId: ULong,
        account: Address,
        tokens: List<Erc20TokenDB>
    ): List<TokenBalance> {
        val callGetEth = Multicall3.getEthBalance.contractCall(Multicall3.address, account)
        val calls = tokens.map { ERC20.balanceOf.contractCall(it.address.asAddress, account) }

        web3.multiCall(chainId, *(calls + callGetEth).toTypedArray())

        val nativeBalance =
            TokenBalance(account, nativeTokensByChainId.getValue(chainId), callGetEth.result)
        return listOf(nativeBalance) + tokens.zip(calls)
            .filter { (_, call) -> (call.resultOrNull ?: BigInteger.ZERO) != BigInteger.ZERO }
            .map { (token, call) -> TokenBalance(account, map(token), call.result) }
    }

    override suspend fun detectNewERC20Tokens(
        chainId: ULong,
        account: Address,
        tokenList: TokenList
    ) = withContext(Dispatchers.Default) {
        val detectedTokens = detectFromTokenList(tokenList, account, chainId)
        saveNewTokens(chainId, detectedTokens)
    }

    internal suspend fun saveNewTokens(
        chainId: ULong,
        detectedTokens: List<Address>
    ) {
        val newTokens = filterNewTokens(detectedTokens, chainId)
        val tokens = getOnChainData(newTokens, chainId)
        saveTokens(tokens)
    }

    private suspend fun detectFromTokenList(
        tokenList: TokenList,
        account: Address,
        chainId: ULong
    ): List<Address> {
        val addresses = tokenListProvider(tokenList)

        val calls = addresses.map { ERC20.balanceOf.contractCall(it, account) }

        web3.multiCall(chainId, *calls.toTypedArray())

        return addresses.zip(calls).mapNotNull { (address, call) ->
            address.takeIf { (call.resultOrNull ?: BigInteger.ZERO) != BigInteger.ZERO }
        }
    }

    private suspend fun filterNewTokens(
        tokens: List<Address>,
        chainId: ULong
    ) = withContext(Dispatchers.Default) {
        tokenQueries.transactionWithResult {
            tokens.filter {
                tokenQueries.getTokenId(chainId.toLong(), it.bytes).executeAsOneOrNull() == null
            }
        }
    }

    private suspend fun getOnChainData(
        newTokens: List<Address>,
        chainId: ULong
    ): List<Erc20Token> {
        val calls = newTokens.map {
            Triple(
                ERC20.name.contractCall(it),
                ERC20.symbol.contractCall(it),
                ERC20.decimals.contractCall(it),
            )
        }

        web3.multiCall(chainId, *calls.flatMap { it.toList() }.toTypedArray())

        return newTokens.zip(calls)
            .filter { (_, call) -> call.toList().all { it.resultOrNull != null } }
            .map { (address, call) ->
                Erc20Token(
                    id = 0u,
                    chainId = chainId,
                    address = address,
                    name = call.first.result,
                    symbol = call.second.result,
                    decimals = call.third.result,
                )
            }
    }

    private suspend fun saveTokens(tokens: List<Erc20Token>) = withContext(Dispatchers.Default) {
        tokenQueries.transaction {
            for (token in tokens) {
                val exists = tokenQueries.getTokenId(token.chainId.toLong(), token.address.bytes)
                    .executeAsOneOrNull() != null

                if (exists.not()) {
                    tokenQueries.insert(
                        chainId = token.chainId.toLong(),
                        address = token.address.bytes,
                        name = token.name,
                        symbol = token.symbol,
                        decimals = token.decimals.toLong()
                    )
                }
            }
        }
    }
}
