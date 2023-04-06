package com.github.nailkhaf.data.tokens.models

internal val nativeTokensByChainId: Map<ULong, NativeToken> by lazy {
    mapOf(
        1uL to NativeToken(
            id = 1uL shr 32,
            chainId = 1uL,
            name = "Ethereum",
            symbol = "ETH",
            decimals = 18u,
        )
    )
}