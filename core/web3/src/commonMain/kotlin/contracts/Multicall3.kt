package com.github.nailkhaf.web3.contracts

import com.github.nailkhaf.web3.abi.*
import com.github.nailkhaf.web3.models.Address
import com.github.nailkhaf.web3.models.asAddress
import com.ionspin.kotlin.bignum.integer.BigInteger
import okio.ByteString
import okio.ByteString.Companion.decodeHex

object Multicall3 {
    val address = "ca11bde05977b3631167028862be2a173976ca11".decodeHex().asAddress
    const val maxCallAmount = 300

    val getEthBalance by functionCall<Address, BigInteger>(
        params = { address("account") },
        paramsTransform = { listOf(it.abi) },
        returns = { uint256("balance") },
        returnsTransform = ::castFirst
    )

    val tryBlockAndAggregate by functionCall<TryBlockAndAggregateParams, TryBlockAndAggregateResult>(
        params = {
            bool("requireSuccess")
            array("calls") {
                tuple {
                    address("target")
                    bytes("data")
                }
            }
        },
        paramsTransform = { params ->
            listOf(
                params.requireSuccess.abi,
                params.calls.map { call ->
                    listOf(
                        call.target.abi,
                        call.data.abi
                    ).tuple
                }.array
            )
        },
        returns = {
            uint256("blockNumber")
            bytes32("blockHash")
            array("returns") {
                tuple {
                    bool("success")
                    bytes("data")
                }
            }
        },
        returnsTransform = { values ->
            TryBlockAndAggregateResult(
                blockNumber = (values[0].value as BigInteger).ulongValue(true),
                blockHash = values[1].value as ByteString,
                returns = (values[2] as AbiValue.Array<*>).value.map { ret ->
                    ret as AbiValue.Tuple
                    Return(
                        success = ret.value[0].value as Boolean,
                        data = ret.value[1].value as ByteString,
                    )
                }
            )
        }
    )

    data class TryBlockAndAggregateParams(
        val requireSuccess: Boolean,
        val calls: List<Call>,
    )

    data class Call(
        val target: Address,
        val data: ByteString,
    )

    data class TryBlockAndAggregateResult(
        val blockNumber: ULong,
        val blockHash: ByteString,
        val returns: List<Return>,
    )

    data class Return(
        val success: Boolean,
        val data: ByteString,
    )
}
