package com.github.nailkhaf.data.tokens.models

import com.github.nailkhaf.web3.models.Address
import com.github.nailkhaf.web3.models.Bytes32
import com.github.nailkhaf.web3.models.asAddress
import com.github.nailkhaf.web3.models.asBytes32
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.github.nailkhaf.database.SelectAll as Erc20TransferToken

data class Erc20TokenTransfer(
    val id: ULong,
    val timestamp: ULong,
    val txHash: Bytes32,
    val token: Erc20Token,
    val from: Address,
    val to: Address,
    val value: BigInteger,
)

fun map(transfer: Erc20TransferToken) = Erc20TokenTransfer(
    id = transfer.erc20TransferId.toULong(),
    timestamp = transfer.timestamp.toULong(),
    txHash = transfer.txHash.asBytes32,
    from = transfer.fromAddress.asAddress,
    to = transfer.toAddress.asAddress,
    value = transfer.value_,
    token = Erc20Token(
        id = transfer.erc20TokenId.toULong(),
        chainId = transfer.chainId.toULong(),
        address = transfer.address.asAddress,
        name = transfer.name,
        symbol = transfer.symbol,
        decimals = transfer.decimals.toUByte()
    )
)