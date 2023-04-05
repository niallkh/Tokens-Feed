package com.github.nailkhaf.web3

import com.github.nailkhaf.web3.models.Address
import com.github.nailkhaf.web3.models.Bytes32
import com.github.nailkhaf.web3.rpc.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import okio.ByteString

class DefaultWeb3Provider(
    private val rpcClient: RpcClient,
    private val networkRpcProvider: NetworkRpcProvider,
    private val json: Json,
) : Web3Provider {

    override suspend fun ethCall(chainId: ULong, target: Address, input: ByteString): ByteString =
        withContext(Dispatchers.Default) {
            val ethCall = EthCall(
                to = target,
                input = input,
            )
            val result = rpcClient.request(
                provider = networkRpcProvider.primary(chainId),
                method = "eth_call",
                json.encodeToJsonElement(ethCall),
                json.encodeToJsonElement("latest"),
            )

            json.decodeFromJsonElement(result)
        }

    override suspend fun ethEstimateGas(
        chainId: ULong,
        target: Address,
        input: ByteString,
    ): ULong = withContext(Dispatchers.Default) {
        val ethCall = EthCall(
            to = target,
            input = input,
        )
        val result = rpcClient.request(
            provider = networkRpcProvider.primary(chainId),
            method = "eth_estimateGas",
            json.encodeToJsonElement(ethCall),
            json.encodeToJsonElement("latest"),
        )

        json.decodeFromJsonElement<String>(result)
            .removePrefix("0x")
            .toULong(16)
    }

    override suspend fun ethBlockNumber(chainId: ULong): Long = withContext(Dispatchers.Default) {
        val result = rpcClient.request(
            provider = networkRpcProvider.primary(chainId),
            method = "eth_blockNumber",
        )

        json.decodeFromJsonElement<String>(result)
            .removePrefix("0x")
            .toLong(16)
    }

    override suspend fun ethGetBlock(chainId: ULong, blockNumber: ULong): EthBlock =
        withContext(Dispatchers.Default) {
            val result = rpcClient.request(
                provider = networkRpcProvider.primary(chainId),
                method = "eth_getBlockByNumber",
                json.encodeToJsonElement("0x${blockNumber.toString(16)}"),
                json.encodeToJsonElement(false),
            )
            json.decodeFromJsonElement(result)
        }


    override suspend fun ethGetTransaction(
        chainId: ULong,
        blockNumber: ULong,
        transactionIndex: UInt
    ): EthTransaction = withContext(Dispatchers.Default) {
        val result = rpcClient.request(
            provider = networkRpcProvider.primary(chainId),
            method = "eth_getTransactionByBlockNumberAndIndex",
            json.encodeToJsonElement("0x${blockNumber.toString(16)}"),
            json.encodeToJsonElement("0x${transactionIndex.toString(16)}"),
        )
        json.decodeFromJsonElement(result)
    }


    override suspend fun ethGetTransactionReceipt(
        chainId: ULong,
        transactionHash: Bytes32
    ): EthTransactionReceipt = withContext(Dispatchers.Default) {
        val result = rpcClient.request(
            provider = networkRpcProvider.primary(chainId),
            method = "eth_getTransactionReceipt",
            json.encodeToJsonElement("0x${transactionHash.bytes.hex()}"),
        )
        json.decodeFromJsonElement(result)
    }


    override suspend fun ethGetLogs(
        chainId: ULong,
        fromBlock: ULong,
        toBlock: ULong,
        address: Address?,
        topics: List<Bytes32?>,
    ): List<EthLog> = withContext(Dispatchers.Default) {
        val result = rpcClient.request(
            provider = networkRpcProvider.primary(chainId),
            method = "eth_getLogs",
            json.encodeToJsonElement(
                EthGetLogs(
                    fromBlock = fromBlock.let { "0x${it.toString(16)}" },
                    toBlock = toBlock.let { "0x${it.toString(16)}" },
                    address = address,
                    topics = topics
                )
            ),
        )
        json.decodeFromJsonElement(result)
    }
}
