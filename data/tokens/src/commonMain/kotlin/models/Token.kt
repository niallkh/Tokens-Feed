package com.github.nailkhaf.data.tokens.models

import com.github.nailkhaf.web3.models.Address
import com.github.nailkhaf.web3.models.asAddress
import com.github.nailkhaf.database.Erc20Token as Erc20TokenDB

sealed interface Token {
    val id: ULong
    val chainId: ULong
    val name: String
    val symbol: String
    val decimals: UByte
}

data class NativeToken(
    override val id: ULong,
    override val chainId: ULong,
    override val name: String,
    override val symbol: String,
    override val decimals: UByte
) : Token

data class Erc20Token(
    override val id: ULong,
    override val chainId: ULong,
    val address: Address,
    override val name: String,
    override val symbol: String,
    override val decimals: UByte
) : Token

fun map(token: Erc20TokenDB): Erc20Token = Erc20Token(
    id = token.erc20TokenId.toULong(),
    chainId = token.chainId.toULong(),
    address = token.address.asAddress,
    name = token.name,
    symbol = token.symbol,
    decimals = token.decimals.toUByte()
)