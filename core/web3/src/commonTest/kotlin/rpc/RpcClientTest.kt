package com.github.nailkhaf.web3.rpc

import com.github.nailkhaf.web3.web3Module
import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.context.GlobalContext
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.mock.declare
import kotlin.test.*

class RpcClientTest : KoinTest {


    @BeforeTest
    fun setup() {
        GlobalContext.startKoin { modules(web3Module) }
    }

    @AfterTest
    fun clean() {
        stopKoin()
    }

    @Test
    fun `success case`() {
        declare<HttpClientEngine> {
            MockEngine {
                respond(
                    content = BLOCK_NUMBER_RESPONSE,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
        }

        runBlocking {
            val result = get<RpcClient>().request(Url("https://1rpc.io/eth"), "eth_blockNumber")

            assertEquals(
                expected = 16981434uL,
                actual = result.jsonPrimitive.content.removePrefix("0x").toULong(16)
            )
        }
    }

    @Test
    fun `state unavailable`() {
        declare<HttpClientEngine> {
            MockEngine {
                respond(
                    content = STATE_UNAVAILABLE_RESPONSE,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
        }

        runBlocking {
            assertFailsWith(Web3StateUnavailableException::class) {
                get<RpcClient>().request(Url("https://1rpc.io/eth"), "eth_blockNumber")
            }
        }
    }

    @Test
    fun `fail case`() {
        declare<HttpClientEngine> {
            MockEngine {
                respond(
                    content = FAILED_RESPONSE,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
        }

        runBlocking {
            assertFailsWith(RpcResponseException::class, "Smth went wrong") {
                get<RpcClient>().request(Url("https://1rpc.io/eth"), "eth_blockNumber")
            }
        }
    }
}

private val BLOCK_NUMBER_RESPONSE = """
    {"jsonrpc":"2.0","result":"0x1031dba","id":0}
""".trimIndent()

private val FAILED_RESPONSE = """
    {"jsonrpc":"2.0","error":{"message":"Smth went wrong"},"id":0}
""".trimIndent()

private val STATE_UNAVAILABLE_RESPONSE = """
    {"jsonrpc":"2.0","result":null,"id":0}
""".trimIndent()
