package com.github.nailkhaf.web3

import com.github.nailkhaf.web3.abi.ContractCall
import com.github.nailkhaf.web3.abi.EventLog
import com.github.nailkhaf.web3.abi.FunctionCall
import com.github.nailkhaf.web3.contracts.Multicall3
import com.github.nailkhaf.web3.models.Address
import com.github.nailkhaf.web3.models.Bytes32
import com.github.nailkhaf.web3.rpc.EthBlock
import com.github.nailkhaf.web3.rpc.EthLog
import com.github.nailkhaf.web3.rpc.EthTransaction
import com.github.nailkhaf.web3.rpc.EthTransactionReceipt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.ByteString

interface Web3Provider {

    suspend fun ethCall(
        chainId: ULong,
        target: Address,
        input: ByteString,
    ): ByteString

    suspend fun ethEstimateGas(
        chainId: ULong,
        target: Address,
        input: ByteString,
    ): ULong

    suspend fun ethBlockNumber(chainId: ULong): Long

    suspend fun ethGetBlock(chainId: ULong, blockNumber: ULong): EthBlock

    suspend fun ethGetTransaction(
        chainId: ULong,
        blockNumber: ULong,
        transactionIndex: UInt
    ): EthTransaction

    suspend fun ethGetTransactionReceipt(
        chainId: ULong,
        transactionHash: Bytes32
    ): EthTransactionReceipt

    suspend fun ethGetLogs(
        chainId: ULong,
        fromBlock: ULong,
        toBlock: ULong,
        address: Address?,
        topics: List<Bytes32?>,
    ): List<EthLog>
}

suspend fun <In : Any, Out : Any> Web3Provider.ethCall(
    chainId: ULong,
    target: Address,
    call: FunctionCall<In, Out>,
    value: In,
): Out = withContext(Dispatchers.Default) {
    call.decode(
        ethCall(
            chainId,
            target,
            call.encode(value)
        )
    )
}

suspend fun <In : Any> Web3Provider.ethEstimateGas(
    chainId: ULong,
    target: Address,
    call: FunctionCall<In, *>,
    value: In,
): ULong = withContext(Dispatchers.Default) {
    ethEstimateGas(
        chainId,
        target,
        call.encode(value)
    )
}

suspend fun <Out : Any> Web3Provider.ethCall(
    chainId: ULong,
    target: Address,
    call: FunctionCall<Unit, Out>,
): Out = withContext(Dispatchers.Default) { ethCall(chainId, target, call, Unit) }

suspend fun Web3Provider.multiCall(
    chainId: ULong,
    vararg contractCalls: ContractCall<*, *>,
): Unit = withContext(Dispatchers.Default) {
    contractCalls.toList().chunked(Multicall3.maxCallAmount).forEach { calls ->
        val result = ethCall(
            chainId = chainId,
            target = Multicall3.address,
            call = Multicall3.tryBlockAndAggregate,
            value = Multicall3.TryBlockAndAggregateParams(
                requireSuccess = false,
                calls = calls.map { call -> Multicall3.Call(call.target, call.encode()) }
            )
        )

        calls.zip(result.returns).map { (call, result) ->
            if (result.success && result.data != ByteString.EMPTY) {
                call.decode(result.data)
            }
        }
    }
}

suspend fun <T : Any, F : Any> Web3Provider.ethGetLogs(
    chainId: ULong,
    fromBlock: ULong,
    toBlock: ULong,
    address: Address?,
    eventLog: EventLog<T, F>,
    filter: F
): List<Pair<T, EthLog>> = withContext(Dispatchers.Default) {
    ethGetLogs(chainId, fromBlock, toBlock, address, eventLog.encodeTopics(filter))
        .map { eventLog.decode(it.topics, it.data) to it }
}
