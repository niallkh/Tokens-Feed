package com.github.nailkhaf.data.tokens.tokenlist

import android.content.Context
import com.github.nailkhaf.web3.models.Address
import com.github.nailkhaf.web3.models.decodeAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

private const val TOKENS_CHUNK_SIZE = 100

class AndroidTokenListProvider(
    private val context: Context
) : TokenListProvider {

    override fun invoke(
        tokenList: TokenList
    ): Flow<List<Address>> = flow {
        val asset = when (tokenList) {
            TokenList.OneInch -> "tokenlists/oneinch.txt"
            TokenList.Rainbow -> "tokenlists/rainbow.txt"
        }

        val callerContext = currentCoroutineContext()
        withContext(Dispatchers.IO) {
            sequence {
                context.assets.open(asset).bufferedReader().use {
                    yield(it.readLine().decodeAddress())
                }
            }
                .chunked(TOKENS_CHUNK_SIZE)
                .forEach { addresses ->
                    withContext(callerContext) {
                        emit(addresses)
                    }
                }
        }
    }
}