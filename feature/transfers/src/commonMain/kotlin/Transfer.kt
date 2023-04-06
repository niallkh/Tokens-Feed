package com.github.nailkhaf.feature.transfers

import com.github.nailkhaf.data.tokens.models.Erc20TokenTransfer
import com.github.nailkhaf.web3.models.formatChecksum
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode

data class Transfer(
    val id: Long,
    val txHash: String,
    val chainId: ULong,
    val tokenAddress: String?,
    val tokenName: String,
    val tokenSymbol: String,
    val from: String,
    val to: String,
    val value: String,
    val date: String
)

fun map(transfer: Erc20TokenTransfer) = Transfer(
    id = transfer.id.toLong(),
    txHash = "0x${transfer.txHash.bytes.hex()}",
    tokenAddress = transfer.token.address.formatChecksum(),
    tokenName = transfer.token.name,
    tokenSymbol = transfer.token.symbol,
    from = transfer.from.formatChecksum(),
    to = transfer.to.formatChecksum(),
    value = transfer.value.let(BigDecimal::fromBigInteger)
        .moveDecimalPoint(-transfer.token.decimals.toInt())
        .roundToDigitPositionAfterDecimalPoint(6, RoundingMode.FLOOR)
        .toPlainString(),
    date = formatDate(transfer.timestamp),
    chainId = transfer.token.chainId
)

internal expect fun formatDate(timestamp: ULong): String
