package com.github.nailkhaf.data.tokens.tokenlist

import android.content.Context
import com.github.nailkhaf.web3.models.Address
import com.github.nailkhaf.web3.models.decodeAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TOKENS_CHUNK_SIZE = 500

class AndroidTokenListProvider(
    private val context: Context
) : TokenListProvider {

    override suspend fun invoke(tokenList: TokenList): List<Address> {
        val asset = when (tokenList) {
            TokenList.OneInch -> "tokenlists/oneinch.txt"
            TokenList.Rainbow -> "tokenlists/rainbow.txt"
        }

        return withContext(Dispatchers.Default) {
            context.assets.open(asset).bufferedReader().useLines { lines ->
                lines.map { it.decodeAddress() }.toList()
            }
        }
    }
}