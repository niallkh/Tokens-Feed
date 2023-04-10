package com.github.nailkhaf.data.tokens

import androidx.datastore.core.Storage
import androidx.datastore.core.okio.OkioStorage
import com.github.nailkhaf.data.tokens.tokenlist.TokenListProvider
import com.github.nailkhaf.database.Database
import com.github.nailkhaf.datastore.StoreSerializer
import com.github.nailkhaf.web3.Web3Provider
import com.github.nailkhaf.web3.contracts.Multicall3
import com.github.nailkhaf.web3.models.Address
import com.github.nailkhaf.web3.models.Bytes32
import com.github.nailkhaf.web3.models.asBytes32
import com.github.nailkhaf.web3.models.decodeAddress
import com.github.nailkhaf.web3.rpc.EthBlock
import com.github.nailkhaf.web3.rpc.EthLog
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import io.mockk.coEvery
import io.mockk.mockkClass
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okio.ByteString.Companion.decodeHex
import okio.FileSystem
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.mock.MockProvider
import org.koin.test.mock.declare
import org.koin.test.mock.declareMock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

private val ACCOUNT = "0xcf4B8167378be0503f5674494188a89a1F401D44".decodeAddress()

class ERC20TransfersRepositoryTest : KoinTest {

    init {
        MockProvider.register { mockkClass(it) }
    }

    @BeforeTest
    fun setup() {
        startKoin { modules(tokensModule) }
        declare<Storage<*>>(named("com.github.nailkhaf.datastore.store")) {
            OkioStorage(FileSystem.SYSTEM, StoreSerializer) {
                val file =
                    FileSystem.SYSTEM_TEMPORARY_DIRECTORY.resolve("com.github.nailkhaf.data.tokens.test.tmp.db")
                FileSystem.SYSTEM.delete(file)
                file
            }
        }
        declareMock<TokenListProvider> {
            coEvery { this@declareMock.invoke(any()) } returns emptyList()
        }
        declare<SqlDriver> {
            JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
                .also { Database.Schema.create(it) }
        }
    }

    @AfterTest
    fun clean() {
        stopKoin()
    }

    @Test
    fun `detect incoming erc20 transfers`() {
        declareMock<Web3Provider> {
            coEvery { ethBlockNumber(1uL) } returns 16984214
            coEvery { ethGetLogs(any(), any(), any(), null, any()) } returns ETH_LOGS
            coEvery { ethCall(1uL, Multicall3.address, any()) } returns ETH_CALL
            coEvery { ethGetBlock(1uL, any()) } returns ETH_BLOCK
        }

        runBlocking {
            val tokensRepository = get<ERC20TransfersRepository>()
            tokensRepository.detectNewIncomingERC20Transfers(1u, ACCOUNT, limit = 1u)
            val transfers = tokensRepository.getTransfers(1u, ACCOUNT).first()
            assertEquals(1, transfers.size)
            assertEquals(BigInteger(20000000), transfers[0].value)
        }
    }
}

private val ETH_LOGS = listOf(
    EthLog(
        blockHash = Bytes32.ZERO,
        transactionHash = "57d56f1962ef6e08a5240e42e78254cd96311a1a392efba50b00d5764a850893".decodeHex().asBytes32,
        blockNumber = BigInteger(16985069),
        transactionIndex = BigInteger.ZERO,
        logIndex = BigInteger(308),
        address = "0xdac17f958d2ee523a2206206994597c13d831ec7".decodeAddress(),
        data = "0000000000000000000000000000000000000000000000000000000001312d00".decodeHex(),
        topics = listOf(
            "ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef".decodeHex().asBytes32,
            "0000000000000000000000007bd85b907e2bf73dbf0b510d3c6b7afef2f6a8b1".decodeHex().asBytes32,
            "000000000000000000000000cf4b8167378be0503f5674494188a89a1f401d44".decodeHex().asBytes32,
        ),
        removed = false,
    )
)

private val ETH_CALL =
    "0000000000000000000000000000000000000000000000000000000001032cc40000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000006000000000000000000000000000000000000000000000000000000000000000030000000000000000000000000000000000000000000000000000000000000060000000000000000000000000000000000000000000000000000000000000012000000000000000000000000000000000000000000000000000000000000001e00000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000000600000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000a54657468657220555344000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000000600000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000455534454000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000006"
        .decodeHex()

private val ETH_BLOCK = EthBlock(
    hash = Bytes32.ZERO,
    gasLimit = BigInteger.ZERO,
    gasUsed = BigInteger.ZERO,
    parentHash = Bytes32.ZERO,
    number = BigInteger(16985069),
    timestamp = BigInteger(1680728375),
    miner = Address.ZERO,
    size = BigInteger.ZERO,
    baseFeePerGas = null,
)
