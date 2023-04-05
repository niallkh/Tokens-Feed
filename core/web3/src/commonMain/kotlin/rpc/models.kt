@file:UseSerializers(
    ByteStringSerializer::class,
    AddressSerializer::class,
    Bytes32Serializer::class,
    BigIntegerSerializer::class
)

package com.github.nailkhaf.web3.rpc

import com.github.nailkhaf.web3.models.*
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.JsonElement
import okio.ByteString

@Serializable
internal data class RpcRequest(
    val jsonrpc: String,
    val method: String,
    val params: List<JsonElement>,
    val id: Int,
)

@Serializable
internal data class RpcResponse(
    val jsonrpc: String,
    val id: Int?,
    val result: JsonElement? = null,
    val error: JsonElement? = null,
)

@Serializable
data class EthCall(
    val from: Address? = null,
    val to: Address,
    val gas: BigInteger? = null,
    val gasPrice: BigInteger? = null,
    val value: BigInteger? = null,
    val input: ByteString,
)

@Serializable
data class EthBlock(
    val hash: Bytes32,
    val gasLimit: BigInteger,
    val gasUsed: BigInteger,
    val parentHash: Bytes32,
    val number: BigInteger,
    val timestamp: BigInteger,
    val miner: Address,
    val size: BigInteger,
    val baseFeePerGas: BigInteger?,
)

@Serializable
data class EthTransaction(
    val hash: Bytes32,
    val blockHash: Bytes32,
    val blockNumber: BigInteger,
    val transactionIndex: BigInteger,
    val from: Address,
    val to: Address?,
    val nonce: BigInteger,
    val value: BigInteger,
    val gas: BigInteger,
    val gasPrice: BigInteger,
    val input: ByteString,
)

@Serializable
data class EthTransactionReceipt(
    val transactionHash: Bytes32,
    val blockHash: Bytes32,
    val contractAddress: Address?,
    val blockNumber: BigInteger,
    val from: Address,
    val to: Address?,
    val transactionIndex: BigInteger,
    val status: BigInteger,
    val type: BigInteger? = null,
    val cumulativeGasUsed: BigInteger,
    val gasUsed: BigInteger,
    val effectiveGasPrice: BigInteger,
    val logs: List<EthLog>,
)

@Serializable
data class EthGetLogs(
    val fromBlock: String,
    val toBlock: String,
    val address: Address?,
    val topics: List<Bytes32?>,
)

@Serializable
data class EthLog(
    val blockHash: Bytes32,
    val transactionHash: Bytes32,
    val blockNumber: BigInteger,
    val transactionIndex: BigInteger,
    val logIndex: BigInteger,
    val address: Address,
    val data: ByteString,
    val topics: List<Bytes32>,
    val removed: Boolean,
)
