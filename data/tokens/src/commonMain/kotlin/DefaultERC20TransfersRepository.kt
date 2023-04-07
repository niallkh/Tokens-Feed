package com.github.nailkhaf.data.tokens

import com.github.nailkhaf.data.tokens.models.Erc20TokenTransfer
import com.github.nailkhaf.data.tokens.models.map
import com.github.nailkhaf.database.Erc20TokenQueries
import com.github.nailkhaf.database.Erc20TransferQueries
import com.github.nailkhaf.datastore.AppStore
import com.github.nailkhaf.datastore.DiscoveryProgress
import com.github.nailkhaf.web3.Web3Provider
import com.github.nailkhaf.web3.contracts.ERC20
import com.github.nailkhaf.web3.models.Address
import com.github.nailkhaf.web3.rpc.EthLog
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private const val LOGS_STEP_SIZE = 2000u
private const val MAX_DISTANCE = 1_000_000u
private const val CONFIRMATION_BLOCKS = 2u

class DefaultERC20TransfersRepository(
    private val web3: Web3Provider,
    private val store: AppStore,
    private val tokensRepository: DefaultERC20TokensRepository,
    private val transferQueries: Erc20TransferQueries,
    private val tokenQueries: Erc20TokenQueries
) : ERC20TransfersRepository {

    override fun getTransfers(
        chainId: ULong,
        account: Address,
        limit: UInt
    ): Flow<List<Erc20TokenTransfer>> = transferQueries
        .selectAll(listOf(chainId.toLong()), account.bytes, limit.toLong())
        .asFlow()
        .mapToList()
        .map { it.map(::map) }

    override suspend fun detectNewIncomingERC20Transfers(
        chainId: ULong,
        account: Address,
        limit: UInt,
    ) = withContext(Dispatchers.Default) {
        val toBlock = web3.ethBlockNumber(chainId).toULong() - CONFIRMATION_BLOCKS
        val fromBlock = getSavedProgress(chainId, account)
            ?: maxOf(toBlock.toLong() - MAX_DISTANCE.toLong(), 0).toULong()

        var counter = 0u
        (toBlock downTo (fromBlock + 1u) step LOGS_STEP_SIZE.toLong())
            .asSequence()
            .map { to -> maxOf(to - LOGS_STEP_SIZE + 1u, fromBlock) to to }
            .takeWhile { counter < limit }
            .forEach { (from, to) ->
                val ethLogs = getIncomingErc20TransferLogs(chainId, from, to, account)
                tokensRepository.saveNewTokens(chainId, ethLogs.map { it.address }.distinct())
                counter += saveTransfers(ethLogs, chainId)
            }

        saveProgress(chainId, account, toBlock)
    }

    private suspend fun getIncomingErc20TransferLogs(
        chainId: ULong,
        from: ULong,
        to: ULong,
        account: Address
    ): List<EthLog> = web3.ethGetLogs(
        chainId = chainId,
        fromBlock = from,
        toBlock = to,
        address = null,
        topics = ERC20.Transfer.encodeTopics(ERC20.Erc20TransferFilter(to = account)),
    )

    private suspend fun saveProgress(
        chainId: ULong,
        account: Address,
        lastBlock: ULong,
    ) {
        store.updateData { store ->
            val progress = store.progresses
                .find { it.account == account.bytes && it.chainId == chainId.toLong() }
            if (progress != null) {
                val updated = progress.copy(lastBlock = lastBlock.toLong())
                store.copy(progresses = store.progresses.filter { it !== progress } + updated)
            } else {
                val new = DiscoveryProgress(
                    chainId = chainId.toLong(),
                    account = account.bytes,
                    lastBlock = lastBlock.toLong()
                )
                store.copy(progresses = store.progresses + new)
            }
        }
    }

    private suspend fun getSavedProgress(
        chainId: ULong,
        account: Address
    ): ULong? {
        val store = store.data.first()
        val progress = store.progresses
            .find { it.account == account.bytes && it.chainId == chainId.toLong() }
        return progress?.lastBlock?.toULong()
    }

    private suspend fun saveTransfers(
        ethLogs: List<EthLog>,
        chainId: ULong,
    ): UInt {
        val timestamps = getTimestamps(ethLogs, chainId)
        return withContext(Dispatchers.Default) {
            transferQueries.transactionWithResult {
                var saved = 0u
                for (ethLog in ethLogs) {
                    val transfer = ERC20.Transfer.decode(ethLog.topics, ethLog.data)
                        .takeIf { it.value != BigInteger.ZERO }
                        ?: continue

                    val tokenId = tokenQueries
                        .getTokenId(chainId.toLong(), ethLog.address.bytes)
                        .executeAsOneOrNull()
                        ?: continue

                    saved++

                    val transferId = transferQueries.getTransferId(
                        ethLog.transactionHash.bytes,
                        ethLog.logIndex.longValue()
                    ).executeAsOneOrNull()
                    if (transferId != null)
                        continue

                    transferQueries.insert(
                        erc20TokenId = tokenId,
                        txHash = ethLog.transactionHash.bytes,
                        logIndex = ethLog.logIndex.longValue(),
                        timestamp = timestamps.getValue(ethLog.blockNumber.ulongValue()).toLong(),
                        fromAddress = transfer.from.bytes,
                        toAddress = transfer.to.bytes,
                        value_ = transfer.value
                    )
                }

                saved
            }
        }
    }

    private suspend fun getTimestamps(
        ethLogs: List<EthLog>,
        chainId: ULong
    ): Map<ULong, ULong> = ethLogs.distinctBy { it.blockNumber }
        .map { web3.ethGetBlock(chainId, it.blockNumber.ulongValue()) }
        .associateBy({ it.number.ulongValue() }, { it.timestamp.ulongValue() })
}