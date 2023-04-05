package com.github.nailkhaf.web3

import com.github.nailkhaf.web3.abi.ContractCall
import com.github.nailkhaf.web3.contracts.ERC20
import com.github.nailkhaf.web3.models.decodeAddress
import com.github.nailkhaf.web3.rpc.RpcClient
import com.ionspin.kotlin.bignum.integer.BigInteger
import io.mockk.coEvery
import io.mockk.mockkClass
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import org.koin.test.get
import org.koin.test.mock.MockProvider
import org.koin.test.mock.declareMock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

private val DAI = "0x6b175474e89094c44da98b954eedeac495271d0f".decodeAddress()
private val ACCOUNT = "0x7dbb4bdcfe614398d1a68ecc219f15280d0959e0".decodeAddress()

class Web3ProviderTest : KoinTest {

    init {
        MockProvider.register { mockkClass(it) }
    }

    @BeforeTest
    fun setup() {
        startKoin { modules(web3Module) }
    }

    @AfterTest
    fun clean() {
        stopKoin()
    }

    @Test
    fun `check koin hierarchy`() {
        getKoin().checkModules()
    }

    @Test
    fun `eth block number`() {
        declareMock<RpcClient> {
            coEvery { request(any(), any(), *anyVararg()) } returns BLOCK_NUMBER
        }

        runBlocking {
            val blockNumber = get<Web3Provider>().ethBlockNumber(1u)

            assertEquals(16981434uL, blockNumber)
        }
    }

    @Test
    fun `eth call`() {
        declareMock<RpcClient> {
            coEvery { request(any(), any(), *anyVararg()) } returns DAI_NAME
        }

        runBlocking {
            val name = get<Web3Provider>().ethCall(1u, DAI, ERC20.name)

            assertEquals("Dai Stablecoin", name)
        }
    }

    @Test
    fun multiCall() {
        declareMock<RpcClient> {
            coEvery { request(any(), any(), *anyVararg()) } returns DAI_NAME_SYMBOL_DECIMALS
        }

        runBlocking {
            val contractCallName = ContractCall(ERC20.name, DAI)
            val contractCallSymbol = ContractCall(ERC20.symbol, DAI)
            val contractCallDecimals = ContractCall(ERC20.decimals, DAI)

            get<Web3Provider>().multiCall(
                1uL,
                contractCallName,
                contractCallSymbol,
                contractCallDecimals
            )

            assertEquals("Dai Stablecoin", contractCallName.result)
            assertEquals("DAI", contractCallSymbol.result)
            assertEquals(18u, contractCallDecimals.result)
        }
    }

    @Test
    fun `eth get logs`() {
        declareMock<RpcClient> {
            coEvery { request(any(), any(), *anyVararg()) } returns ETH_LOGS
        }

        runBlocking {
            val transfers = get<Web3Provider>().ethGetLogs(
                chainId = 1uL,
                fromBlock = 16977700uL - 500000uL,
                toBlock = 16977700uL,
                address = null,
                eventLog = ERC20.Transfer,
                filter = ERC20.Erc20TransferFilter(to = ACCOUNT)
            )
                .map { (transfer, _) -> transfer }

            assertEquals(
                "0x94b4fff9896b897c1a4b4de46ac29cb89bf82fa4".decodeAddress(),
                transfers[0].from
            )
            assertEquals(ACCOUNT, transfers[0].to)
            assertEquals(BigInteger.parseString("400000000000000000000"), transfers[0].value)
            assertEquals(
                "0x94b4fff9896b897c1a4b4de46ac29cb89bf82fa4".decodeAddress(),
                transfers[1].from
            )
            assertEquals(ACCOUNT, transfers[1].to)
            assertEquals(BigInteger.parseString("400000000000000000000"), transfers[1].value)
        }
    }
}

private val BLOCK_NUMBER = "0x1031dba"
    .let { Json.encodeToJsonElement(it) }

private val DAI_NAME_SYMBOL_DECIMALS =
    "0x0000000000000000000000000000000000000000000000000000000001030ed10000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000006000000000000000000000000000000000000000000000000000000000000000030000000000000000000000000000000000000000000000000000000000000060000000000000000000000000000000000000000000000000000000000000012000000000000000000000000000000000000000000000000000000000000001e00000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000000600000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000e44616920537461626c65636f696e0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000000600000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000344414900000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000012"
        .let { Json.encodeToJsonElement(it) }

private val DAI_NAME =
    "0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000e44616920537461626c65636f696e000000000000000000000000000000000000"
        .let { Json.encodeToJsonElement(it) }

private val ETH_LOGS = Json.decodeFromString<JsonElement>(
    """
[
    {
        "address": "0x33eb301b1ba6326998b01f7c4d53c13a28a5cb54",
        "blockHash": "0x8c8a6dfda0e4f9f37c1b960d1e3791001502ca97a9a29d434ae4caf338f444aa",
        "blockNumber": "0x1030a64",
        "data": "0x000000000000000000000000000000000000000000000015af1d78b58c400000",
        "logIndex": "0xfe",
        "removed": false,
        "topics": [
            "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef",
            "0x00000000000000000000000094b4fff9896b897c1a4b4de46ac29cb89bf82fa4",
            "0x0000000000000000000000007dbb4bdcfe614398d1a68ecc219f15280d0959e0"
        ],
        "transactionHash": "0x7676e6c1c9ee192a1246d055c0d0119e2260501b36117b86efc38ffad4c444dd",
        "transactionIndex": "0x7c"
    },
    {
        "address": "0x33eb301b1ba6326998b01f7c4d53c13a28a5cb54",
        "blockHash": "0xb8658a053e374cc84066b3df7a32253bf47b2c1a7fd3cb0dfbd635062055797e",
        "blockNumber": "0x1030a95",
        "data": "0x000000000000000000000000000000000000000000000015af1d78b58c400000",
        "logIndex": "0x1cd",
        "removed": false,
        "topics": [
            "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef",
            "0x00000000000000000000000094b4fff9896b897c1a4b4de46ac29cb89bf82fa4",
            "0x0000000000000000000000007dbb4bdcfe614398d1a68ecc219f15280d0959e0"
        ],
        "transactionHash": "0xe6af0c4b1dcec08038bdfcdc42cef66640af6804a97fc05a8d0d302375ecd02c",
        "transactionIndex": "0xe4"
    }
]
""".trimIndent()
)
