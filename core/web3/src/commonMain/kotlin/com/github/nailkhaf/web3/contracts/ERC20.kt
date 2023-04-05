package com.github.nailkhaf.web3.contracts

import com.github.nailkhaf.web3.abi.abi
import com.github.nailkhaf.web3.abi.castFirst
import com.github.nailkhaf.web3.abi.eventLog
import com.github.nailkhaf.web3.abi.functionCall
import com.github.nailkhaf.web3.models.Address
import com.ionspin.kotlin.bignum.integer.BigInteger

object ERC20 {

    val name by functionCall<String>(
        returns = { string("name") },
    )
    val symbol by functionCall<String>(
        returns = { string("returns") },
    )
    val decimals by functionCall(
        returns = { uint8("decimals") },
        returnsTransform = { (it.first().value as BigInteger).uintValue(true) }
    )
    val totalSupply by functionCall<BigInteger>(
        returns = { uint256("totalSupply") },
    )
    val balanceOf by functionCall<Address, BigInteger>(
        params = { address("account") },
        paramsTransform = { listOf(it.abi) },
        returns = { uint256("balance") },
        returnsTransform = ::castFirst
    )

    val Transfer by eventLog(
        params = {
            address(indexed = true)
            address(indexed = true)
            uint256()
        },
        paramsTransform = { values ->
            Erc20Transfer(
                values[0].value as Address,
                values[1].value as Address,
                values[2].value as BigInteger
            )
        },
        filterTransform = { filter: Erc20TransferFilter ->
            listOf(filter.from?.abi, filter.to?.abi)
        }
    )

    data class Erc20Transfer(
        val from: Address,
        val to: Address,
        val value: BigInteger
    )

    data class Erc20TransferFilter(
        val from: Address? = null,
        val to: Address? = null,
    )
}
