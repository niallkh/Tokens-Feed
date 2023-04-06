package com.github.nailkhaf.data.tokens.models

import com.github.nailkhaf.web3.models.Address
import com.ionspin.kotlin.bignum.integer.BigInteger

data class TokenBalance(
    val account: Address,
    val token: Token,
    val value: BigInteger
)
