package com.github.nailkhaf.feature.balances

import com.github.nailkhaf.data.tokens.models.Erc20Token
import com.github.nailkhaf.data.tokens.models.NativeToken
import com.github.nailkhaf.data.tokens.models.TokenBalance
import com.github.nailkhaf.web3.models.formatChecksum
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode

data class Balance(
    val id: Long,
    val account: String,
    val chainId: ULong,
    val tokenAddress: String?,
    val tokenName: String,
    val tokenSymbol: String,
    val balance: String,
)

fun map(balance: TokenBalance) = Balance(
    id = balance.token.id.toLong(),
    account = balance.account.formatChecksum(),
    tokenAddress = when (val token = balance.token) {
        is Erc20Token -> token.address.formatChecksum()
        is NativeToken -> null
    },
    tokenName = balance.token.name,
    tokenSymbol = balance.token.symbol,
    balance = balance.value.let(BigDecimal::fromBigInteger)
        .moveDecimalPoint(-balance.token.decimals.toInt())
        .roundToDigitPositionAfterDecimalPoint(6, RoundingMode.FLOOR)
        .toPlainString(),
    chainId = balance.token.chainId
)
