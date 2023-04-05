package com.github.nailkhaf.web3

import io.ktor.http.*

interface NetworkRpcProvider {
    suspend fun primary(chainId: ULong): Url
}

internal class DefaultNetworkRpcProvider : NetworkRpcProvider {
    override suspend fun primary(chainId: ULong): Url {
        require(chainId == 1uL) { "Network provider is not available for $chainId" }
        return Url("https://1rpc.io/eth")
    }
}
